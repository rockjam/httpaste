package com.github.rockjam.httpaste.curl

import com.github.rockjam.httpaste.{HttpRequestBlueprint, parsing}
import fastparse.core.Parsed

import scala.reflect.macros.blackbox.Context

object CurlValidationMacro {

  def macroImpl(c: Context)(): c.Expr[HttpRequestBlueprint] = {
    import c.universe._

    val Apply(_, List(expr @ Apply(_, List(Literal(Constant(rawString: String)))))) =
      c.prefix.tree

    val isTripleQuote = expr.pos.source.content.startsWith("curl\"\"\"", expr.pos.start)

    val startPosition = {
      val quotesLength = if (isTripleQuote) 3 else 1
      c.enclosingPosition.point + quotesLength + "curl".length
    }

    CurlParser.commandParser.parse(rawString) match {
      case Parsed.Success(parts, _) =>
        val uri = parts.collectFirst { case uri: parsing.URI => uri }
        uri match {
          case Some(_) =>
            c.Expr(q"_root_.com.github.rockjam.httpaste.curl.parseValidated($rawString)")
          case None =>
            c.abort(c.enclosingPosition.withPoint(startPosition), "URI is required in command!")
        }
      case Parsed.Failure(lastParser, index, _) =>
        c.abort(c.enclosingPosition.withPoint(startPosition + index),
                s"Failed to parse $lastParser")
    }
  }

}
