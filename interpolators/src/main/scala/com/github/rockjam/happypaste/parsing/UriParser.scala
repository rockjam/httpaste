package com.github.rockjam.happypaste.parsing

import com.github.rockjam.happypaste.model.NewURI
import fastparse.all._

object UriParser {

  val parser: Parser[NewURI] = {
    val alfa      = CharIn('a' to 'z')
    val alfaUpper = CharIn('A' to 'Z')
    val num       = CharIn('0' to '9')

    val alfaNum = alfa | alfaUpper | num
    //    val reserved = CharIn(Seq('/', ':', '.'))
    val pctEncoded = CharIn('0' to '9', 'A' to 'F', Seq('%'))

    val scheme = ("https" | "http").!

    val `hier-part` = "//".? ~ (alfaNum | CharIn("./()_-")).rep(min = 1).!

    val queryString = (pctEncoded | alfaNum | CharIn(".=")).rep(min = 1).!

    val fragment = AnyChar.rep(min = 1).!

    ((scheme ~ ":").? ~ `hier-part` ~ ("?" ~ queryString).? ~ ("#" ~ fragment).?)
      .map(NewURI.tupled)
  }

}
