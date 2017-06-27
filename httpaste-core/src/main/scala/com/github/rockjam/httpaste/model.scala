package com.github.rockjam.httpaste

final case class HttpMethod(name: String)

final case class HttpHeader(name: String, value: String)

final case class Data(value: String)

final case class URI(scheme: Option[String],
                     authority: String, // it's both authority and path for now.
                     query: Option[String],
                     fragment: Option[String]) {
  val value: String = {
    val scheme   = this.scheme.getOrElse("http")
    val query    = this.query.map("?" + _).getOrElse("")
    val fragment = this.fragment.map("#" + _).getOrElse("")
    s"${scheme}://${authority}${query}${fragment}"
  }
}

object RequestOptions {
  val empty = RequestOptions(followRedirect = false)
}

final case class RequestOptions(followRedirect: Boolean)

object HttpRequestBlueprint {
  def default(uri: URI): HttpRequestBlueprint =
    HttpRequestBlueprint(method = HttpMethod("GET"),
                         uri = uri,
                         headers = Seq.empty,
                         options = RequestOptions.empty,
                         data = None)
}

final case class HttpRequestBlueprint(method: HttpMethod,
                                      uri: URI, // TODO: validate uri;
                                      headers: Seq[HttpHeader],
                                      options: RequestOptions,
                                      data: Option[Data]) // requestEntity/entity ???
