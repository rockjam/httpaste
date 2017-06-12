package com.github.rockjam.happypaste.macroimpl

import scala.reflect.macros.blackbox.Context

case class LiteralUtil(c: Context) {
  import c.universe._
  def getString: String = {
    val Apply(_, List(Apply(_, List(Literal(Constant(s: String)))))) = c.prefix.tree
    s
  }
}
