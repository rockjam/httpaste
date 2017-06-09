package com.github.rockjam.happypaste.curl

sealed trait RequestPart

sealed abstract class HttpMethod(val name: String) extends RequestPart

object HttpMethod {
  def fromString: String => HttpMethod = {
    case "GET"  => GET
    case "POST" => POST
    case "PUT"  => PUT
  }

  case object GET  extends HttpMethod("GET")
  case object POST extends HttpMethod("POST")
  case object PUT  extends HttpMethod("PUT")
}

final case class HttpHeader(name: String, value: String) extends RequestPart

final case class Data(value: String) extends RequestPart // it's actually sequence of values

case object FollowRedirect extends RequestPart

final case class URI(value: String) extends RequestPart

object HttpRequestBlueprint {
  val empty: HttpRequestBlueprint =
    HttpRequestBlueprint(HttpMethod.GET, URI(""), Seq.empty, followRedirect = false, data = None)
}

final case class HttpRequestBlueprint(
    method: HttpMethod,
    uri: URI, // TODO: validate uri; add http/https if not present
    headers: Seq[HttpHeader],
    followRedirect: Boolean,
    data: Option[Data]) // requestEntity/entity ???
