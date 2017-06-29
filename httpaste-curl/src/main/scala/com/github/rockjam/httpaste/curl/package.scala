package com.github.rockjam.httpaste

import com.github.rockjam.httpaste.parsing.RequestPart
import fastparse.all._

import scala.language.experimental.macros

package object curl {

  implicit class CurlStringContext(val ctx: StringContext) extends AnyVal {
    def curl(): HttpRequestBlueprint = macro CurlValidationMacro.macroImpl
  }

  def parseValidated(inputString: String): HttpRequestBlueprint = {
    val Parsed.Success(parts, _) = CurlParser.commandParser.parse(inputString)
    toModel(parts)
  }

  private def toModel(parts: Seq[RequestPart]): HttpRequestBlueprint = {
    val requestURI = parts.collectFirst {
      case uri: parsing.URI =>
        URI(
          scheme = uri.scheme,
          authority = uri.authority,
          query = uri.query,
          fragment = uri.fragment
        )
    }.get
    (parts foldLeft HttpRequestBlueprint.default(requestURI)) { (req, part) =>
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
  }

}
