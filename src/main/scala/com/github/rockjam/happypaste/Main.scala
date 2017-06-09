package com.github.rockjam.happypaste

import com.github.rockjam.happypaste

object Main extends App {

  import happypaste.curl._
  import scalaj.http._

  val request1 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com"""
  val request2 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com """
  val request3 = curl"""curl https://google.com -L -XGET -H 'Content-Type: application/json'"""

  val requestWithData =
    curl"""curl -XPOST
          -H "Content-Type: application/json"
          --data '{ "name": "rockjam", "old": false }'
          http://example.com/users"""

  println(request1)
  println(request2)
  println(request3)
  println(requestWithData)

  def asScalajHttp(blueprint: HttpRequestBlueprint): HttpRequest = {
    val options =
      if (blueprint.followRedirect) Seq(HttpOptions.followRedirects(true)) else Seq.empty

    val prepared = Http(blueprint.uri.value)
      .method(blueprint.method.name)
      .headers(blueprint.headers.map(e => e.name -> e.value))
      .options(options)

    blueprint.data map { data =>
      prepared.copy(connectFunc = StringBodyConnectFunc(data.value))
    } getOrElse prepared
  }

  val scalajRequest = asScalajHttp(requestWithData)
  println(scalajRequest)
  val response = scalajRequest.execute()
  println(response.code)
  println(response.body)

}
