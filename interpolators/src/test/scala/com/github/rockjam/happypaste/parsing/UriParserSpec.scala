package com.github.rockjam.happypaste.parsing

import com.github.rockjam.happypaste.model.NewURI
import fastparse.all.Parsed
import org.scalactic.source.Position
import org.scalatest.{FlatSpec, Inspectors, Matchers}

class UriParserSpec extends FlatSpec with Matchers with Inspectors {
  behavior of "UriParser"

  import UriParser._

  it should "parse uri with scheme and authority" in {
    val uri = expectSuccess(parser.parse("https://github.com"))
    uri shouldEqual NewURI(
      scheme = Some("https"),
      authority = "github.com",
      query = None,
      fragment = None
    )
  }

  it should "not fail when scheme is not provided" in {
    val uri = expectSuccess(parser.parse("github.com"))
    uri shouldEqual NewURI(
      scheme = None,
      authority = "github.com",
      query = None,
      fragment = None
    )
  }

  it should "parse uri with scheme and authority and path" in {
    val uri = expectSuccess(parser.parse("https://github.com/rockjam/metaservices"))
    uri shouldEqual NewURI(
      scheme = Some("https"),
      authority = "github.com/rockjam/metaservices",
      query = None,
      fragment = None
    )
  }

  it should "parse uri with scheme and authority and path and query" in {
    val uri = expectSuccess(parser.parse("https://github.com/rockjam/metaservices?utf8=%E2%9C%93"))
    uri shouldEqual NewURI(
      scheme = Some("https"),
      authority = "github.com/rockjam/metaservices",
      query = Some("utf8=%E2%9C%93"),
      fragment = None
    )
  }

  it should "parse uri with scheme and authority and path and query and fragment " in {
    val uri =
      expectSuccess(parser.parse("https://github.com/rockjam/metaservices?utf8=%E2%9C%93#usage"))
    uri shouldEqual NewURI(
      scheme = Some("https"),
      authority = "github.com/rockjam/metaservices",
      query = Some("utf8=%E2%9C%93"),
      fragment = Some("usage")
    )
  }

  it should "parse valid uri's" in {
    val validUri = List(
      "http://foo.com/blah_blah",
      "http://foo.com/blah_blah/",
      "http://foo.com/blah_blah_(wikipedia)",
      "http://foo.com/blah_blah_(wikipedia)_(again)",
      "http://www.example.com/wpstyle/?p=364"
    )

    forAll(validUri) { uri =>
      val result = parser.parse(uri)
      println(result)
      result shouldBe a[Parsed.Success[_]]
    }
  }

  private def expectSuccess[T](parsed: Parsed[T])(implicit pos: Position) = {
    parsed shouldBe a[Parsed.Success[_]]
    parsed.get.value
  }

}
