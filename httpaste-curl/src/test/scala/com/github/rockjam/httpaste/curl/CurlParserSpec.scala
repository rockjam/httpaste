package com.github.rockjam.httpaste.curl

import fastparse.all.Parsed
import org.scalatest.{FlatSpec, Inspectors, Matchers}

class CurlParserSpec extends FlatSpec with Matchers with Inspectors {

  behavior of "CurlParser"

  "method parser" should "not parse invalid method options" in {
    val mustFail = List(
      "curl", // not a method option
      "JAKLDJLK", // not anything
      "-XPOT", // wrong http method
      "--requestGET" // --request must be separated from method name
    )

    forEvery(mustFail) { s =>
      CurlParser.method.parse(s) shouldBe a[Parsed.Failure]
    }
  }

  it should "parse methods in different forms" in {
    val mustParse = List(
      "--request GET",
      "-XGET",
      "-X GET",
      "--request POST",
      "-XPOST",
      "-X POST",
      "--request PUT",
      "-XPUT",
      "-X PUT",
      "-X OPTIONS",
      "-XOPTIONS",
      "--request OPTIONS"
    )

    forEvery(mustParse) { s =>
      CurlParser.method.parse(s) shouldBe a[Parsed.Success[_]]
    }
  }

  it should "not parse HEAD method" in {
    val heads = List(
      "--request HEAD",
      "-XHEAD",
      "-X HEAD"
    )

    forEvery(heads) { s =>
      CurlParser.method.parse(s) shouldBe a[Parsed.Failure]
    }
  }

  "header parser" should "not parse invalid method options" in {
    val mustFail = List(
      "--header'Content-Type: application/json'"
    )

    forEvery(mustFail) { s =>
      CurlParser.method.parse(s) shouldBe a[Parsed.Failure]
    }
  }

  it should "parse headers in different forms" in {
    val mustParse = List(
      "-H 'Content-Type: application/json'",
      "-H'Content-Type: application/json'",
      "--header 'Content-Type: application/json'",
      "--header \"Content-Type: application/json\"",
      "-H \"Content-Type: application/json\""
    )

    forEvery(mustParse) { s =>
      CurlParser.header.parse(s) shouldBe a[Parsed.Success[_]]
    }
  }

  //  val Parsed.Success(_, _) = location.parse("-L")
  //  val Parsed.Success(_, _) = location.parse("--location")

}
