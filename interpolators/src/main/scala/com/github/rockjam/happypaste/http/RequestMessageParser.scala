package com.github.rockjam.happypaste.http

import com.github.rockjam.happypaste.parsing._
import fastparse.all._

object RequestMessageParser {

  val CRLF = P("\n")

  val SP = P(" ".rep(min = 1))

  val requestLine = {
    val method = (
      "OPTIONS" |
        "GET" |
        "HEAD" |
        "POST" |
        "PUT" |
        "DELETE" |
        "TRACE" |
        "CONNECT"
    ).!.map(HttpMethod.fromString)
    val requestUri  = CharsWhile(_ != ' ').!.map(URI) // TODO: use UriParser instead
    val httpVersion = ("HTTP/1.0" | "HTTP/1.1" | "HTTP/2").!

    method ~ SP ~ requestUri ~ SP ~ httpVersion ~ CRLF
  }

  val header = {
    val headerName  = CharsWhile(_ != ':')
    val headerValue = CharsWhile(_ != '\n')
    (!CRLF ~ headerName.! ~ ":" ~ headerValue.! ~ CRLF).map(HttpHeader.apply)
  }

  val messageBody = (CharsWhile(_ != '\n') ~ CRLF).!.rep

  val requestMessage = (requestLine ~ header.rep ~ CRLF ~ messageBody.?)
    .map {
      case (method, uri, httpVersion, headers, optMessage) =>
        HttpRequestBlueprint(method,
                             uri,
                             headers,
                             options = RequestOptions.empty,
                             data = optMessage.map(lines => Data(lines.mkString)))
    }
}
