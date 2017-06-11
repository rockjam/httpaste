package com.github.rockjam.happypaste

import com.github.rockjam.happypaste.model.HttpRequestBlueprint
import fastparse.core.Parsed

package object curl {

  implicit class CurlStringContext(val ctx: StringContext) extends AnyVal {
    def curl(): HttpRequestBlueprint = CurlParser.command.parse(ctx.parts.mkString("")) match {
      case Parsed.Success(result, _) => result
      case Parsed.Failure(_, _, _)   => throw new RuntimeException("Failed to parse curl request")
    }
  }

}
