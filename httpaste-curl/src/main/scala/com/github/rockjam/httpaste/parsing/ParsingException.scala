package com.github.rockjam.httpaste.parsing

case class ParsingException(inputString: String, index: Int, lastParser: String)
    extends RuntimeException(s"""
       |Failed to parse ${lastParser}
       |${inputString}
       |${" " * index + "^"}
     """.stripMargin)
