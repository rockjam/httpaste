package com.github.rockjam

import org.scalatest.{FunSpec, Matchers}

class HelloSpec extends FunSpec with Matchers {

  describe("example hierarchy") {

    it("should work") {
      1 + 1 shouldBe 2
    }

  }

}