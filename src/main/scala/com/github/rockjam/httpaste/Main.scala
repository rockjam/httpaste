package com.github.rockjam.httpaste

object Main extends App {

  import curl._

  val request1 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com"""
  val request2 = curl"curl -L -XGET -H 'Content-Type: application/json' https://google.com"

//  val request2 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com """
//  val request3 = curl"""curl https://google.com -L -XGET -H 'Content-Type: application/json'"""
//
//  val requestWithData =
//    curl"""curl -XPOST
//          -H "Content-Type: application/json"
//          -H "Accept-Language: ru,en-US;q=0.8,en;q=0.6"
//          --data '{ "name": "rockjam", "old": false }'
//          http://example.com/users"""
//
//  println(request1)
//  println(request2)
//  println(request3)
//
//  {
//    val github  = curl"curl -L -XGET api.github.com/rate_limit"
//    val request = github.asScalajHttp
//    println(request)
//    println(s"=== from scalaj: ${request.asString}")
//  }
//
//  {
//    // akka http doesn't handle follow redirect thing
//    val github  = curl"curl -L -XGET https://api.github.com/rate_limit"
//    val request = github.asAkkaHttp
//    println(s"=== akka request: ${request}")
//
//    implicit val system = ActorSystem("requests")
//    implicit val mat    = ActorMaterializer()
//    import system.dispatcher
//    val http = Http(system)
//
//    val response = for {
//      resp <- http.singleRequest(request)
//      _ = println(resp.headers)
//      _ = println(resp.status)
//      body <- resp.entity.dataBytes.runFold("")(_ + _.utf8String)
//    } yield body
//
//    import scala.concurrent.duration._
//    val result = Await.result(response, 10.seconds)
//
//    println(s"==== from akka http: ${result}")
//  }

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

//  val scalajRequest = requestWithData.asScalajHttp
//  println(scalajRequest)
//  val response = scalajRequest.execute()
//  println(response.code)
//  println(response.body)

}
