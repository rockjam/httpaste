package com.github.rockjam.happypaste.curl

import com.github.rockjam.happypaste.model._
import fastparse.all._

object CurlParser {

  val curl = P("curl")

  val ws = " ".rep

  val methods = {
    val requestPrefixes = {
      val `--X`       = P("-X")
      val `--request` = P("--request")
      (`--X` ~ ws) | (`--request` ~ ws)
    }

    val get  = "GET"
    val post = "POST"
    val put  = "PUT"
    (requestPrefixes ~ (get | post | put).!).map(HttpMethod.fromString)
  }

//  val Parsed.Success(_, _) = methods.parse("--request GET")
//  val Parsed.Success(_, _) = methods.parse("-XGET")
//  val Parsed.Success(_, _) = methods.parse("-X GET")
//
//  val Parsed.Success(_, _) = methods.parse("--request POST")
//  val Parsed.Success(_, _) = methods.parse("-XPOST")
//  val Parsed.Success(_, _) = methods.parse("-X POST")
//
//  val Parsed.Success(_, _) = methods.parse("--request PUT")
//  val Parsed.Success(_, _) = methods.parse("-XPUT")
//  val Parsed.Success(_, _) = methods.parse("-X PUT")
//
//  val Parsed.Success(_, _) = (curl ~ ws ~ methods).parse("curl -XGET")
//  val Parsed.Success(_, _) = (curl ~ ws ~ methods).parse("curl -X GET")
//  val Parsed.Success(_, _) = (curl ~ ws ~ methods).parse("curl -X POST")
//  val Parsed.Success(_, _) = (curl ~ ws ~ methods).parse("curl -XPOST")

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

//  val Parsed.Success(_, _)    = header.parse("-H 'Content-Type: application/json'")
//  val Parsed.Success(_, _)    = header.parse("-H'Content-Type: application/json'")
//  val Parsed.Failure(_, _, _) = header.parse("--header'Content-Type: application/json'")
//  val Parsed.Success(_, _)    = header.parse("--header 'Content-Type: application/json'")
//  val Parsed.Success(_, _)    = header.parse("""--header "Content-Type: application/json"""")
//  val Parsed.Success(_, _)    = header.parse("""-H "Content-Type: application/json"""")

  val location = {
    val `-L`         = P("-L")
    val `--location` = P("--location")
    (`-L` | `--location`).map(_ => FollowRedirect)
  }

  val unknownFlags = ("--" ~ CharsWhile(_ != ' ')).!.map(UnknownFlag)

//  val Parsed.Success(_, _) = location.parse("-L")
//  val Parsed.Success(_, _) = location.parse("--location")

  val naiveUri = CharsWhile(_ != ' ').!.map(URI)

  val commandParser: Parser[HttpRequestBlueprint] = {
    val commandParameters = (methods ~ ws) | (header ~ ws) | (location ~ ws) | (data ~ ws) | (unknownFlags ~ ws) | (naiveUri ~ ws)
    val parser            = curl ~ ws ~ commandParameters.rep(min = 1)
    parser.map { parts =>
      (parts foldLeft HttpRequestBlueprint.empty) { (req, part) =>
        part match {
          case uri: URI           => req.copy(uri = uri)
          case method: HttpMethod => req.copy(method = method)
          case data: Data         => req.copy(data = Some(data))
          case FollowRedirect     => req.copy(followRedirect = true)
          case header: HttpHeader => req.copy(headers = req.headers :+ header)
          case unknown: UnknownFlag =>
            println(s"Got unknown flag: ${unknown}")
            req
        }
      }
    }
  }

}
