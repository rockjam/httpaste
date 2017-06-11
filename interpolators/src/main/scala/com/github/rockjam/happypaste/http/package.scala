package com.github.rockjam.happypaste

import com.github.rockjam.happypaste.model.HttpRequestBlueprint
import fastparse.core.Parsed

package object http {

  implicit class RequestMessageStringContext(val ctx: StringContext) extends AnyVal {
    def http(): HttpRequestBlueprint =
      RequestMessageParser.requestMessage.parse(ctx.parts.mkString("")) match {
        case Parsed.Success(result, _) => result
        case Parsed.Failure(_, _, _) =>
          throw new RuntimeException("Failed to parse http request message")
      }
  }

}
