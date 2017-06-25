package com.github.rockjam.httpaste

import fastparse.core.Parsed

package object curl {

  implicit class CurlStringContext(val ctx: StringContext) extends AnyVal {
    def curl(): HttpRequestBlueprint =
      CurlParser.commandParser.parse(ctx.parts.mkString) match {
        case Parsed.Success(parts, _) =>
          (parts foldLeft HttpRequestBlueprint.empty) { (req, part) =>
            part match {
              case method: parsing.HttpMethod =>
                req.copy(method = HttpMethod(method.name))
              case data: parsing.Data =>
                req.copy(data = Some(Data(data.value)))
              case parsing.FollowRedirect =>
                req.copy(options = req.options.copy(followRedirect = true))
              case header: parsing.HttpHeader =>
                req.copy(headers = req.headers :+ HttpHeader(header.name, header.value))
              case unknown: parsing.UnknownFlag =>
                println(s"Got unknown flag: ${unknown}")
                req
              case uri: parsing.URI =>
                req.copy(
                  uri = URI(
                    scheme = uri.scheme,
                    authority = uri.authority,
                    query = uri.query,
                    fragment = uri.fragment
                  ))
              case parsing.Ingorable => req
            }
          }
        case Parsed.Failure(_, _, _) => throw new RuntimeException("Failed to parse curl request")
      }
  }

}
