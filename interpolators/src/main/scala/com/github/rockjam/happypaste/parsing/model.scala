package com.github.rockjam.happypaste.parsing

import scala.collection.immutable

sealed trait RequestPart extends Product with Serializable

sealed abstract class HttpMethod(val name: String) extends RequestPart

object HttpMethod {
  def fromString: String => HttpMethod = {
    case "OPTIONS" => OPTIONS
    case "GET"     => GET
    case "HEAD"    => HEAD
    case "POST"    => POST
    case "PUT"     => PUT
    case "DELETE"  => DELETE
    case "TRACE"   => TRACE
    case "CONNECT" => CONNECT
  }

  case object OPTIONS extends HttpMethod("OPTIONS")
  case object GET     extends HttpMethod("GET")
  case object HEAD    extends HttpMethod("HEAD")
  case object POST    extends HttpMethod("POST")
  case object PUT     extends HttpMethod("PUT")
  case object DELETE  extends HttpMethod("DELETE")
  case object TRACE   extends HttpMethod("TRACE")
  case object CONNECT extends HttpMethod("CONNECT")

  val all: immutable.Seq[HttpMethod] = List(
    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT
  )
}

object HttpHeader {
  def apply(nameValue: (String, String)): HttpHeader =
    HttpHeader(nameValue._1.trim, nameValue._2.trim)
}

final case class HttpHeader private (name: String, value: String) extends RequestPart

final case class Data(value: String) extends RequestPart // it's actually sequence of values

case object FollowRedirect extends RequestPart

final case class UnknownFlag(value: String) extends RequestPart

case object Ingorable extends RequestPart // TODO: find ways to remove it

final case class URI(value: String) extends RequestPart

final case class NewURI(scheme: Option[String],
                        authority: String, // it's both authority and path for now.
                        query: Option[String],
                        fragment: Option[String])
    extends RequestPart

object HttpRequestBlueprint {
  val empty: HttpRequestBlueprint =
    HttpRequestBlueprint(method = HttpMethod.GET,
                         uri = URI(""),
                         headers = Seq.empty,
                         options = RequestOptions.empty,
                         data = None)
}

object RequestOptions {
  val empty = RequestOptions(followRedirect = false)
}
case class RequestOptions(followRedirect: Boolean)

final case class HttpRequestBlueprint(
    method: HttpMethod,
    uri: URI, // TODO: validate uri; add http/https if not present
    headers: Seq[HttpHeader],
    options: RequestOptions, // ??? should it be there, or move to options?
    data: Option[Data]) // requestEntity/entity ???
