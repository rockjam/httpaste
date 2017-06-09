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

final case class URI(uri: String) extends RequestPart

object HttpRequest {
  val empty: HttpRequest = HttpRequest(HttpMethod.GET, URI(""), Seq.empty, false)
}

final case class HttpRequest(method: HttpMethod,
                             uri: URI,
                             headers: Seq[HttpHeader],
                             followRedirect: Boolean)
