package com.github.rockjam.httpaste.parsing

import scala.collection.immutable

sealed trait RequestPart extends Product with Serializable

case class HttpMethod(name: String) extends RequestPart

object HttpMethod {
  val all: immutable.Seq[String] = List(
    "OPTIONS",
    "GET",
    "HEAD",
    "POST",
    "PUT",
    "DELETE",
    "TRACE",
    "CONNECT"
  )
}

final case class URI(scheme: Option[String],
                     authority: String, // it's both authority and path for now.
                     query: Option[String],
                     fragment: Option[String])
    extends RequestPart

object HttpHeader {
  def apply(nameValue: (String, String)): HttpHeader =
    HttpHeader(nameValue._1.trim, nameValue._2.trim)
}

final case class HttpHeader private (name: String, value: String) extends RequestPart

final case class Data(value: String) extends RequestPart // it's actually sequence of values

case object FollowRedirect extends RequestPart

final case class UnknownFlag(value: String) extends RequestPart

case object Ingorable extends RequestPart // TODO: find ways to remove it
