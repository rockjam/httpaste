package com.github.rockjam.httpaste

import scalaj.http._

package object scalajhttp {

  implicit final class ScalajHttpOps(val blueprint: HttpRequestBlueprint) extends AnyVal {
    def asScalajHttp: HttpRequest = {
      val options =
        if (blueprint.options.followRedirect) Seq(HttpOptions.followRedirects(true)) else Seq.empty

      val prepared = Http(blueprint.uri.value)
        .method(blueprint.method.name)
        .headers(blueprint.headers.map(e => e.name -> e.value))
        .options(options)

      blueprint.data map { data =>
        prepared.copy(connectFunc = StringBodyConnectFunc(data.value))
      } getOrElse prepared
    }
  }

}
