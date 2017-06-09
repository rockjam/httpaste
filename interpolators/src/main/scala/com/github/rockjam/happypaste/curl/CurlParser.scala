package com.github.rockjam.happypaste.curl

import fastparse.all._
import fastparse.core.Parser

object CurlParser {

  val curl = P("curl")

  val ws = " ".rep

  val methods = {
    val requestPrefixes = {
      val minusX            = P("-X")
      val minusMinusRequest = P("--request")
      (minusX ~ ws) | (minusMinusRequest ~ ws)
    }

    val get  = requestPrefixes ~ "GET".!
    val post = requestPrefixes ~ "POST".!
    val put  = requestPrefixes ~ "PUT".!
    (get | post | put).map(HttpMethod.fromString)
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
      val minusH           = P("-H")
      val minusMinusHeader = P("--header")
      (minusH ~ ws) | (minusMinusHeader ~ " ".rep(min = 1))
    }
    def quotedHeader(q: Char) = {
      val headerName           = CharsWhile(_ != ':')
      val colonWs              = ":" ~ ws
      def headerValue(q: Char) = CharsWhile(_ != q)
      s"$q" ~ headerName.! ~ colonWs ~ headerValue(q).! ~ s"$q"
    }
    val singleQuotedHeader = quotedHeader(''')
    val doubleQuotedHeader = quotedHeader('"')

    (headerPrefixes ~ (singleQuotedHeader | doubleQuotedHeader)).map(HttpHeader.tupled)
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
    val minusL             = P("-L")
    val minusMinusLocation = P("--location")
    (minusL | minusMinusLocation).map(_ => FollowRedirect)
  }

//  val Parsed.Success(_, _) = location.parse("-L")
//  val Parsed.Success(_, _) = location.parse("--location")

  val naiveUri = CharsWhile(_ != ' ').!.map(URI)

  val command: Parser[HttpRequestBlueprint, Char, String] = {
    val curlParameters = (methods ~ ws) | (header ~ ws) | (location ~ ws) | (data ~ ws) | (naiveUri ~ ws)
    val parser         = (curl ~ ws) ~ curlParameters.rep
    parser.map { parts =>
      (parts foldLeft HttpRequestBlueprint.empty) { (req, part) =>
        part match {
          case uri: URI           => req.copy(uri = uri)
          case method: HttpMethod => req.copy(method = method)
          case data: Data         => req.copy(data = Some(data))
          case FollowRedirect     => req.copy(followRedirect = true)
          case header: HttpHeader => req.copy(headers = req.headers :+ header)
        }
      }
    }
  }

}
