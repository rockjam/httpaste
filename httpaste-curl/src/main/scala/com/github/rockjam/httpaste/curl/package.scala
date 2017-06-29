package com.github.rockjam.httpaste

import contextual.Prefix

package object curl {

  implicit class CurlStringContext(val ctx: StringContext) {
    val curl = Prefix(CurlInterpolator, ctx)
  }

}
