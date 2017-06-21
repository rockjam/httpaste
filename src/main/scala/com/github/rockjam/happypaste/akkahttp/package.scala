package com.github.rockjam.happypaste

import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import com.github.rockjam.happypaste.parsing.HttpRequestBlueprint

package object akkahttp {

  implicit final class AkkaHttpOps(val blueprint: HttpRequestBlueprint) extends AnyVal {
    // TODO: add Follow redirect
    def asAkkaHttp: HttpRequest = {
      val entity = blueprint.data
        .map { data =>
          HttpEntity(data.value)
        }
        .getOrElse(HttpEntity.Empty)

      HttpRequest(
        method = HttpMethod.custom(blueprint.method.name),
        uri = Uri(blueprint.uri.value),
        headers = blueprint.headers.map { header =>
          val ParsingResult.Ok(result, _) = HttpHeader.parse(header.name, header.value)
          result
        }.toVector,
        entity = entity
      )
    }
  }

}
