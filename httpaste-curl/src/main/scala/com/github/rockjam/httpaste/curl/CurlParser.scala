package com.github.rockjam.httpaste.curl

import com.github.rockjam.httpaste.parsing._
import fastparse.all._

object CurlParser {

  val curl = P("curl")

  val ws = P(" ".rep)

  val method = {
    val requestPrefixes = P {
      val `--X`       = P("-X")
      val `--request` = P("--request")
      (`--X` ~ ws) | (`--request` ~ " ".rep(min = 1))
    }

    val methods = P {
      val ms = HttpMethod.all.filterNot(_ == "HEAD")
      ms.tail.foldLeft(P(ms.head))(_ | _).!
    }

    (requestPrefixes ~/ methods).map(HttpMethod.apply).opaque("request method")
  }

  val header = {
    val headerPrefixes = P {
      val `-H`       = P("-H")
      val `--header` = P("--header")
      (`-H` ~ ws) | (`--header` ~ " ".rep(min = 1))
    }

    def quotedHeader(q: Char) = {
      val headerName           = CharsWhile(_ != ':')
      def headerValue(q: Char) = CharsWhile(_ != q)
      s"$q" ~ headerName.! ~ ":" ~ ws ~ headerValue(q).! ~ s"$q"
    }
    val singleQuotedHeader = P(quotedHeader('''))
    val doubleQuotedHeader = P(quotedHeader('"'))

    (headerPrefixes ~/ (singleQuotedHeader | doubleQuotedHeader))
      .map(HttpHeader.apply)
      .opaque("http header")
  }

// don't interpret @
  val data = {
    val dataPrefixes = P {
      val `-d`            = P("-d")
      val `--data`        = P("--data")
      val `--data-binary` = P("--data-binary")
      (`-d` ~ ws) | (`--data-binary` ~ " ".rep(min = 1)) | (`--data` ~ " ".rep(min = 1))
    }
    def quotedData(q: Char) = s"$q" ~ CharsWhile(_ != q).! ~ s"$q"

    val singleQuotedData = P(quotedData('''))
    val doubleQuotedData = P(quotedData('"'))

    (dataPrefixes ~/ "$".? ~ (singleQuotedData | doubleQuotedData))
      .map(Data)
      .opaque("data")
  }

  val location = {
    val `-L`         = P("-L")
    val `--location` = P("--location")
    (`-L` | `--location`).map(_ => FollowRedirect).opaque("location flag")
  }

  val unknownFlags = ("--" ~ CharsWhile(_ != ' ')).!.map(UnknownFlag)

  val uri = P(UriParser.parser | "'" ~ UriParser.parser ~ "'" | "\"" ~ UriParser.parser ~ "\"")

  val ignore = P("\\" | "\n").map(_ => Ingorable) // TODO: find a ways to get rid of ignorable

  val commandParser: Parser[Seq[RequestPart]] = {
    val commandParameters = method | header | location | data | unknownFlags | ignore | uri
    curl ~ ws ~ commandParameters.rep(min = 1, sep = ws)
  }

}
