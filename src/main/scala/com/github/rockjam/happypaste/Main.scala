package com.github.rockjam.happypaste

import com.github.rockjam.happypaste
import com.github.rockjam.happypaste.model.HttpRequestBlueprint

object Main extends App {

  import happypaste.curl._
  import happypaste.http._
  import scalaj.http._

  val request1 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com"""
  val request2 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com """
  val request3 = curl"""curl https://google.com -L -XGET -H 'Content-Type: application/json'"""

  val requestWithData =
    curl"""curl -XPOST
          -H "Content-Type: application/json"
          -H "Accept-Language: ru,en-US;q=0.8,en;q=0.6"
          --data '{ "name": "rockjam", "old": false }'
          http://example.com/users"""

  println(request1)
  println(request2)
  println(request3)
  println(requestWithData)

  val httpRequest =
    http"""POST https://edit.telegra.ph/check HTTP/1.1
      Host: edit.telegra.ph
      Connection: keep-alive
      Content-Length: 29
      Accept: application/json, text/javascript, */*; q=0.01
      Origin: http://telegra.ph
      User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36
      Content-Type: application/x-www-form-urlencoded; charset=UTF-8
      Referer: http://telegra.ph/Test-these-cookies-12-04
      Accept-Encoding: gzip, deflate, br
      Accept-Language: ru,en-US;q=0.8,en;q=0.6
      Cookie: tph_uuid=w5WBtTKUzjlO30mzcWmqvCQgxOEfXzurEo05hIZO1i

      page_id=a24d9e31620af10761693
      """

  println(s"http request: ${httpRequest}")
  println(asScalajHttp(httpRequest))
  println(asScalajHttp(httpRequest).asString)

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

  curl"""curl http://docs.scala-lang.org/overviews/reflection/annotations-names-scopes.html \
          -H 'Accept-Encoding: gzip, deflate, sdch' \
          -H 'Accept-Language: ru,en-US;q=0.8,en;q=0.6' \
          -H 'Upgrade-Insecure-Requests: 1' \
          -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36' \
          -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8' \
          -H 'Referer: http://docs.scala-lang.org/overviews/quasiquotes/intro.html' \
          -H 'Cookie: __utma=213681430.1941647691.1473203395.1496604277.1496861531.58; __utmc=213681430; __utmz=213681430.1496490906.56.28.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); _ga=GA1.2.1941647691.1473203395; __utma=213182593.1941647691.1473203395.1497304170.1497388432.10; __utmc=213182593; __utmz=213182593.1496875360.6.5.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)' \
          -H 'Connection: keep-alive' \
          -H 'If-Modified-Since: Tue, 13 Jun 2017 20:36:57 GMT' \
          -H 'Cache-Control: max-age=0'"""

// won't compile
//  val malformedCurl = curl"curl "
//
//  val malformedHttp =
//    http"""https://edit.telegra.ph/check HTTP/1.1
//      Host: edit.telegra.ph
//      Connection: keep-alive
//      Content-Length: 29
//      Accept: application/json, text/javascript, */*; q=0.01
//      Origin: http://telegra.ph
//      User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36
//      Content-Type: application/x-www-form-urlencoded; charset=UTF-8
//      Referer: http://telegra.ph/Test-these-cookies-12-04
//      Accept-Encoding: gzip, deflate, br
//      Accept-Language: ru,en-US;q=0.8,en;q=0.6
//      Cookie: tph_uuid=w5WBtTKUzjlO30mzcWmqvCQgxOEfXzurEo05hIZO1i
//
//      page_id=a24d9e31620af10761693
//      """

  val scalajRequest = asScalajHttp(requestWithData)
  println(scalajRequest)
  val response = scalajRequest.execute()
  println(response.code)
  println(response.body)

}
