package com.github.rockjam.happypaste.curl

import com.github.rockjam.happypaste.parsing._
import org.scalatest.{FlatSpec, Matchers}

class CurlInterpolatorSpec extends FlatSpec with Matchers {

  behavior of "curl interpolator"

  it should "parse simplest command and imply GET method" in {
    val req = curl"curl http://httpbin.org/get"

    req.uri shouldEqual URI("http://httpbin.org/get")
    req.data shouldBe empty
    req.method shouldEqual HttpMethod.GET
    req.headers shouldBe empty
    req.options shouldBe RequestOptions.empty
  }

  it should "work with quoted uri" in {
    val req = curl"curl 'http://httpbin.org/get'"

    req.uri shouldEqual URI("http://httpbin.org/get")
  }

  it should "work with double-quoted uri" in {
    val req = curl"""curl "http://httpbin.org/get""""

    req.uri shouldEqual URI("http://httpbin.org/get")
  }

  it should "work when uri doesn't have scheme and imply http" in {
    val req = curl"curl httpbin.org/get"

    req.uri shouldEqual URI("http://httpbin.org/get")
  }

  it should "parse command with method provided" in {
    val req = curl"curl -XGET http://httpbin.org/get"

    req.uri shouldEqual URI("http://httpbin.org/get")
    req.data shouldBe empty
    req.method shouldEqual HttpMethod.GET
    req.headers shouldBe empty
    req.options shouldBe RequestOptions.empty
  }

  it should "parse command with method and headers" in {
    val req =
      curl"curl -XGET -H 'Accept-Language: en-US' -H 'Referer: http://httpbin.org/' http://httpbin.org/get"

    req.uri shouldEqual URI("http://httpbin.org/get")
    req.data shouldBe empty
    req.method shouldEqual HttpMethod.GET
    val expectedHeaders = Seq(
      HttpHeader("Accept-Language", "en-US"),
      HttpHeader("Referer", "http://httpbin.org/")
    )
    req.headers should contain theSameElementsAs expectedHeaders
    req.options shouldBe RequestOptions.empty
  }

  it should "parse multiline command without \\ at the end of line" in {
    val request =
      curl"""curl -X POST
          -H "Content-Type: application/json"
          -H "Accept-Language: en-US"
          --data '{ "name": "rockjam", "old": false }'
          http://httpbin.org/post"""

    request.uri shouldEqual URI("http://httpbin.org/post")
    request.data shouldEqual Some(Data("""{ "name": "rockjam", "old": false }"""))
    request.method shouldEqual HttpMethod.POST
    request.options shouldEqual RequestOptions.empty
    val expectedHeaders = Seq(
      HttpHeader("Content-Type", "application/json"),
      HttpHeader("Accept-Language", "en-US")
    )
    request.headers should contain theSameElementsAs expectedHeaders

  }

  it should "parse multiline command with \\ at the end of line" in {
    val request =
      curl"""curl -XGET http://docs.scala-lang.org/overviews/reflection/annotations-names-scopes.html \
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

    request.uri shouldEqual URI(
      "http://docs.scala-lang.org/overviews/reflection/annotations-names-scopes.html")
    request.data shouldBe empty
    request.method shouldEqual HttpMethod.GET
    request.options shouldEqual RequestOptions.empty
    val expectedHeaders = Seq(
      HttpHeader("Accept-Encoding", "gzip, deflate, sdch"),
      HttpHeader("Accept-Language", "ru,en-US;q=0.8,en;q=0.6"),
      HttpHeader("Upgrade-Insecure-Requests", "1"),
      HttpHeader(
        "User-Agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"),
      HttpHeader("Accept",
                 "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
      HttpHeader("Referer", "http://docs.scala-lang.org/overviews/quasiquotes/intro.html"),
      HttpHeader(
        "Cookie",
        "__utma=213681430.1941647691.1473203395.1496604277.1496861531.58; __utmc=213681430; __utmz=213681430.1496490906.56.28.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); _ga=GA1.2.1941647691.1473203395; __utma=213182593.1941647691.1473203395.1497304170.1497388432.10; __utmc=213182593; __utmz=213182593.1496875360.6.5.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)"
      ),
      HttpHeader("Connection", "keep-alive"),
      HttpHeader("If-Modified-Since", "Tue, 13 Jun 2017 20:36:57 GMT"),
      HttpHeader("Cache-Control", "max-age=0")
    )
    request.headers should contain theSameElementsAs expectedHeaders
  }

  it should "parse command from main" in {
    val req = curl"""curl https://edit.telegra.ph/save \
    -H 'Origin: http://telegra.ph' \
    -H 'Accept-Encoding: gzip, deflate, br' \
    -H 'Accept-Language: ru,en-US;q=0.8,en;q=0.6' \
    -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36' \
    -H 'Content-Type: multipart/form-data; boundary=---------------------------TelegraPhBoundary21' \
    -H 'Accept: application/json, text/javascript, */*; q=0.01' \
    -H 'Referer: http://telegra.ph/Pervyj-scala-meetup-v-Spb-06-19' \
    -H 'Cookie: tph_uuid=w5WBtTKUzjlO30mzcWmqvCQgxOEfXzurEo05hIZO1i' \
    -H 'Connection: keep-alive' \
    --data-binary '-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="Data";filename="content.html"\r\nContent-type: plain/text\r\n\r\n[{"tag":"p","children":["\u0412\u0441\u0435\u043c \u0432\u0441\u0435 \u043f\u043e\u043d\u0440\u0430\u0432\u0438\u043b\u043e\u0441\u044c, \u0431\u044b\u043b\u043e \u043c\u043d\u043e\u0433\u043e \u043b\u044e\u0434\u0435\u0439 \u0438 \u043a\u043e\u0448\u0435\u043a"]}]\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="title"\r\n\r\n\u041f\u0435\u0440\u0432\u044b\u0439 scala meetup \u0432 \u0421\u043f\u0431\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="author"\r\n\r\nNikolay\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="author_url"\r\n\r\n\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="page_id"\r\n\r\n33de93bbcc4f263210876\r\n-----------------------------TelegraPhBoundary21--'"""

    curl"""curl 'https://edit.telegra.ph/save' -H 'Origin: http://telegra.ph' -H 'Accept-Encoding: gzip, deflate, br' -H 'Accept-Language: ru,en-US;q=0.8,en;q=0.6' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36' -H 'Content-Type: multipart/form-data; boundary=---------------------------TelegraPhBoundary21' -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Referer: http://telegra.ph/Pervyj-scala-meetup-v-Spb-06-19' -H 'Cookie: tph_uuid=w5WBtTKUzjlO30mzcWmqvCQgxOEfXzurEo05hIZO1i' -H 'Connection: keep-alive' --data-binary $$'-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="Data";filename="content.html"\r\nContent-type: plain/text\r\n\r\n[{"tag":"p","children":["\u0412\u0441\u0435\u043c \u0432\u0441\u0435 \u043f\u043e\u043d\u0440\u0430\u0432\u0438\u043b\u043e\u0441\u044c, \u0431\u044b\u043b\u043e \u043c\u043d\u043e\u0433\u043e \u043b\u044e\u0434\u0435\u0439 \u0438 \u043a\u043e\u0448\u0435\u043a"]}]\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="title"\r\n\r\n\u041f\u0435\u0440\u0432\u044b\u0439 SCALA MEETUP \u0432 \u0421\u043f\u0431\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="author"\r\n\r\nNikolay\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="author_url"\r\n\r\n\r\n-----------------------------TelegraPhBoundary21\r\nContent-Disposition: form-data; name="page_id"\r\n\r\n33de93bbcc4f263210876\r\n-----------------------------TelegraPhBoundary21--' --compressed"""
    println(req)
//    val req1 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com"""
//    val request2 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com """
//    val request3 = curl"""curl https://google.com -L -XGET -H 'Content-Type: application/json'"""
//    curl"""curl http://docs.scala-lang.org/overviews/reflection/annotations-names-scopes.html \
//          -H 'Accept-Encoding: gzip, deflate, sdch' \
//          -H 'Accept-Language: ru,en-US;q=0.8,en;q=0.6' \
//          -H 'Upgrade-Insecure-Requests: 1' \
//          -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36' \
//          -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8' \
//          -H 'Referer: http://docs.scala-lang.org/overviews/quasiquotes/intro.html' \
//          -H 'Cookie: __utma=213681430.1941647691.1473203395.1496604277.1496861531.58; __utmc=213681430; __utmz=213681430.1496490906.56.28.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); _ga=GA1.2.1941647691.1473203395; __utma=213182593.1941647691.1473203395.1497304170.1497388432.10; __utmc=213182593; __utmz=213182593.1496875360.6.5.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)' \
//          -H 'Connection: keep-alive' \
//          -H 'If-Modified-Since: Tue, 13 Jun 2017 20:36:57 GMT' \
//          -H 'Cache-Control: max-age=0'"""

  }

}
