package com.github.rockjam.happypaste.macroimpl

import com.github.rockjam.happypaste.curl.CurlParser
import com.github.rockjam.happypaste.http.RequestMessageParser
import com.github.rockjam.happypaste.model.HttpRequestBlueprint
import fastparse.all.Parser
import fastparse.core.Parsed

import scala.reflect.macros.blackbox.Context

object ParserMacro {

  def curlImpl(c: Context)(): c.Expr[HttpRequestBlueprint] =
    macroImpl(c, "curl", CurlParser.command)

  def httpImpl(c: Context)(): c.Expr[HttpRequestBlueprint] =
    macroImpl(c, "http", RequestMessageParser.requestMessage)

  private def macroImpl(c: Context,
                        name: String,
                        parser: Parser[HttpRequestBlueprint]): c.Expr[HttpRequestBlueprint] = {
    import c.universe._
    val string      = LiteralUtil(c).getString
    val isMultiline = string.split("\n").length > 1
    val result      = parser.parse(string)

    result match {
      case Parsed.Success(value, _) =>
        val HttpRequestBlueprint(method, uri, headers, follow, optData) = value
        val res = {
          val headerTrees = headers.map { h =>
            q"HttpHeader(${h.name}, ${h.value})"
          }
          val dataTree = optData match {
            case None       => q"None"
            case Some(data) => q"Some(Data(${data.value}))"
          }
          val methodTree = q"HttpMethod.fromString(${method.name})"
          val uriTree    = q"URI(${uri.value})"

          c.Expr(q"""
            import _root_.com.github.rockjam.happypaste.model._
            HttpRequestBlueprint(
                method = $methodTree,
                uri = $uriTree,
                headers = _root_.scala.Seq(..$headerTrees),
                followRedirect = $follow,
                data = $dataTree
              )
         """)
        }
        println(res)
        res
      case Parsed.Failure(_, index, extra) =>
        val quotesLength  = if (isMultiline) 3 else 1
        val startOfString = c.enclosingPosition.point + name.length + quotesLength
        c.abort(c.enclosingPosition.withPoint(startOfString + index),
                s"Failed to parse ${name}-interpolated string")
    }
  }

}
