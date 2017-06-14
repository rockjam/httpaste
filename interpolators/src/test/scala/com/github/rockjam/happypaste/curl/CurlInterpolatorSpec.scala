package com.github.rockjam.happypaste.curl

import com.github.rockjam.happypaste.model.{HttpMethod, RequestOptions, URI}
import org.scalatest.{FlatSpec, Matchers}

class CurlInterpolatorSpec extends FlatSpec with Matchers {

  behavior of "curl interpolator"

  it should "parse simplest curl command and imply GET method" in {
    val req = curl"curl https://google.com"

    req.uri shouldEqual URI("https://google.com")
    req.data shouldBe empty
    req.method shouldEqual HttpMethod.GET
    req.headers shouldBe empty
    req.options shouldBe RequestOptions.empty
  }

}
