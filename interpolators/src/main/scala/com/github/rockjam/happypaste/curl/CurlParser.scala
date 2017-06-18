package com.github.rockjam.happypaste.curl

import com.github.rockjam.happypaste.model._
import com.github.rockjam.happypaste.parsing.UriParser
import fastparse.all._

object CurlParser {

  val curl = P("curl")

  val ws = " ".rep

  val method = {
    val requestPrefixes = {
      val `--X`       = P("-X")
      val `--request` = P("--request")
      (`--X` ~ ws) | (`--request` ~ " ".rep(min = 1))
    }

    val methods = {
      val ms = HttpMethod.all.map(_.name).filterNot(_ == "HEAD")
      ms.tail.foldLeft(P(ms.head))(_ | _)
    }

    (requestPrefixes ~ methods.!).map(HttpMethod.fromString)
  }

  val header = {
    val headerPrefixes = {
      val `-H`       = P("-H")
      val `--header` = P("--header")
      (`-H` ~ ws) | (`--header` ~ " ".rep(min = 1))
    }
    def quotedHeader(q: Char) = {
      val headerName           = CharsWhile(_ != ':')
      def headerValue(q: Char) = CharsWhile(_ != q)
      s"$q" ~ headerName.! ~ ":" ~ ws ~ headerValue(q).! ~ s"$q"
    }
    val singleQuotedHeader = quotedHeader(''')
    val doubleQuotedHeader = quotedHeader('"')

    (headerPrefixes ~ (singleQuotedHeader | doubleQuotedHeader)).map(HttpHeader.apply)
  }

// don't interpret @
  val data = {
    val dataPrefixes = {
      val `-d`     = P("-d")
      val `--data` = P("--data")
      (`-d` ~ ws) | (`--data` ~ " ".rep(min = 1))
    }
    def quotedData(q: Char) = s"$q" ~ CharsWhile(_ != q).! ~ s"$q"

    val singleQuotedData = quotedData(''')
    val doubleQuotedData = quotedData('"')

    (dataPrefixes ~ (singleQuotedData | doubleQuotedData)).map(Data)
  }

  val location = {
    val `-L`         = P("-L")
    val `--location` = P("--location")
    (`-L` | `--location`).map(_ => FollowRedirect)
  }

  val unknownFlags = ("--" ~ CharsWhile(_ != ' ')).!.map(UnknownFlag)

  val uri = (UriParser.parser | "'" ~ UriParser.parser ~ "'" | "\"" ~ UriParser.parser ~ "\"")

  val backSlash = P("\\").map(_ => Ingorable) // TODO: find a ways to get rid of ignorable

  val newLine = P("\n").map(_ => Ingorable) // TODO: find a ways to get rid of ignorable

  val commandParser: Parser[HttpRequestBlueprint] = {
    val commandParameters = (method | header | location | data | unknownFlags | backSlash | newLine | uri) ~ ws
    val parser            = curl ~ ws ~ commandParameters.rep(min = 1)
    parser.map { parts =>
      (parts foldLeft HttpRequestBlueprint.empty) { (req, part) =>
        part match {
          case uri: URI           => req.copy(uri = uri)
          case method: HttpMethod => req.copy(method = method)
          case data: Data         => req.copy(data = Some(data))
          case FollowRedirect     => req.copy(options = req.options.copy(followRedirect = true))
          case header: HttpHeader => req.copy(headers = req.headers :+ header)
          case unknown: UnknownFlag =>
            println(s"Got unknown flag: ${unknown}")
            req
          case newURI: NewURI =>
            val scheme   = newURI.scheme.getOrElse("http")
            val query    = newURI.query.map("?" + _).getOrElse("")
            val fragment = newURI.fragment.map("#" + _).getOrElse("")
            req.copy(uri = URI(s"${scheme}://${newURI.authority}${query}${fragment}"))
          case Ingorable => req
        }
      }
    }
  }

}
