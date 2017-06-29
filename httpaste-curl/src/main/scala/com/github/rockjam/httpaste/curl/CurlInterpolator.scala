package com.github.rockjam.httpaste.curl

import contextual._
import fastparse.all._
import com.github.rockjam.httpaste._
import com.github.rockjam.httpaste.{HttpRequestBlueprint, parsing}

object CurlInterpolator extends Interpolator {

  def contextualize(interpolation: StaticInterpolation) = {
    val lit @ Literal(_, curlCommand) = interpolation.parts.head
    CurlParser.commandParser.parse(curlCommand) match {
      case Parsed.Success(parts, _) =>
        val noURI = parts.collectFirst { case uri: parsing.URI => uri }.isEmpty
        if (noURI) {
          interpolation.abort(lit, 0, "URI is required in command!")
        }
      case Parsed.Failure(lastParser, index, _) =>
        interpolation.abort(lit, index, s"Failed to parse $lastParser")
    }
    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): HttpRequestBlueprint = {
    val Parsed.Success(parts, _) = CurlParser.commandParser.parse(interpolation.literals.head)
    toModel(parts)
  }

  private def toModel(parts: Seq[parsing.RequestPart]): HttpRequestBlueprint = {
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
        case _: parsing.URI =>
          req
        case parsing.Ingorable => req
      }
    }
  }

}
