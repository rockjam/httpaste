package com.github.rockjam.happypaste

import com.github.rockjam.happypaste

object Main extends App {

  import happypaste.curl._

  val request1 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com"""
  val request2 = curl"""curl -L -XGET -H 'Content-Type: application/json' https://google.com """
  val request3 = curl"""curl https://google.com -L -XGET -H 'Content-Type: application/json'"""

  println(request1)
  println(request2)
  println(request3)


}
