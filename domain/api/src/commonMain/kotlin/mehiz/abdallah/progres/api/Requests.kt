package mehiz.abdallah.progres.api

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.set
import io.ktor.http.takeFrom

@Suppress("FunctionNaming")
internal fun GET(
  url: String,
  headers: Headers = Headers.Empty,
): HttpRequestBuilder {
  return HttpRequestBuilder()
    .apply {
      this.method = HttpMethod.Get
      this.url.set { takeFrom(url) }
      this.headers.appendAll(headers)
    }
}

@Suppress("FunctionNaming")
internal fun POST(
  url: String,
  body: String,
  headers: Headers = Headers.Empty,
  contentType: ContentType = ContentType.Application.Json,
): HttpRequestBuilder {
  return HttpRequestBuilder()
    .apply {
      this.method = HttpMethod.Post
      this.url.set { takeFrom(url) }
      this.headers.appendAll(headers)
      this.setBody(body)
      contentType(contentType)
    }
}

@Suppress("FunctionNaming")
internal fun PUT(
  url: String,
  body: String,
  headers: Headers = Headers.Empty,
  contentType: ContentType = ContentType.Application.Json,
): HttpRequestBuilder {
  return HttpRequestBuilder().apply {
    this.method = HttpMethod.Put
    this.url.set { takeFrom(url) }
    this.headers.appendAll(headers)
    this.setBody(body)
    contentType(contentType)
  }
}

@Suppress("FunctionNaming")
internal fun DELETE(
  url: String,
  body: String,
  headers: Headers = Headers.Empty,
  contentType: ContentType = ContentType.Application.Json,
): HttpRequestBuilder {
  return HttpRequestBuilder().apply {
    this.method = HttpMethod.Delete
    this.url.set { takeFrom(url) }
    this.headers.appendAll(headers)
    this.setBody(body)
    contentType(contentType)
  }
}
