package com.github.rockjam.happypaste

import com.github.rockjam.happypaste.macroimpl.ParserMacro
import com.github.rockjam.happypaste.model.HttpRequestBlueprint

import scala.language.experimental.macros

package object curl {

  implicit class CurlStringContext(val ctx: StringContext) extends AnyVal {
    def curl(): HttpRequestBlueprint = macro ParserMacro.curlImpl
  }

}
