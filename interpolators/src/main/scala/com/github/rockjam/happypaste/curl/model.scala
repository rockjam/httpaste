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

case object FollowRedirect extends RequestPart

final case class URI(value: String) extends RequestPart

object HttpRequestBlueprint {
  val empty: HttpRequestBlueprint = HttpRequestBlueprint(HttpMethod.GET, URI(""), Seq.empty, false)
}

final case class HttpRequestBlueprint(method: HttpMethod,
                                      uri: URI,
                                      headers: Seq[HttpHeader],
                                      followRedirect: Boolean)
