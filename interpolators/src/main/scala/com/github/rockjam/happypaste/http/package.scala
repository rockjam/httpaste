package com.github.rockjam.happypaste

import com.github.rockjam.happypaste.macroimpl.ParserMacro
import com.github.rockjam.happypaste.model.HttpRequestBlueprint

import scala.language.experimental.macros

package object http {

  implicit class RequestMessageStringContext(val ctx: StringContext) extends AnyVal {
    def http(): HttpRequestBlueprint = macro ParserMacro.httpImpl
  }

}
