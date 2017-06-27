package com.github.rockjam.httpaste

import com.github.rockjam.httpaste.parsing.{ParsingException, RequestPart}
import fastparse.core.Parsed

package object curl {

  implicit class CurlStringContext(val ctx: StringContext) extends AnyVal {
    def curl(): HttpRequestBlueprint = {
      val inputString = ctx.parts.mkString
      CurlParser.commandParser.parse(inputString) match {
        case Parsed.Success(parts, _) =>
          toModel(parts) match {
            case Left(err)        => throw new RuntimeException(err)
            case Right(blueprint) => blueprint
          }
        case fail @ Parsed.Failure(lastParser, index, extra) =>
          throw ParsingException(inputString, index, lastParser.toString)
      }
    }

    private def toModel(parts: Seq[RequestPart]): Either[String, HttpRequestBlueprint] = {
      val requestURI = parts
        .collectFirst {
          case uri: parsing.URI =>
            URI(
              scheme = uri.scheme,
              authority = uri.authority,
              query = uri.query,
              fragment = uri.fragment
            )
        }
      requestURI match {
        case Some(uri) =>
          val blueprint = (parts foldLeft HttpRequestBlueprint.default(uri)) { (req, part) =>
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
                req
              case parsing.Ingorable => req
            }
          }
          Right(blueprint)
        case None =>
          Left("URI is required in command!")
      }
    }
  }

}
