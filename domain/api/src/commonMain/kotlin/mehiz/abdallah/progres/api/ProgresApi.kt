@file:OptIn(ExperimentalUuidApi::class)

package mehiz.abdallah.progres.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import mehiz.abdallah.progres.api.dto.UserAuthDto
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProgresApi(
  private val client: HttpClient,
) {
  val bearerToken: (String) -> Headers = {
    Headers.build { append("authorization", it) }
  }

  suspend fun login(id: String, password: String): UserAuthDto {
    return client.request(
      POST(
        Endpoints.Login.buildUrl(),
        body = """{"username": "$id", "password": "$password"}""",
      ),
    ).body<UserAuthDto>()
  }

  private suspend fun getBase64EncodedStudentPhoto(uuid: Uuid, token: String): String {
    return client.get(
      Endpoints.GetStudentPhoto.buildUrl(uuid.toString()),
    ) {
      headers {
        appendAll(bearerToken(token))
        append(HttpHeaders.Accept, "image/png")
      }
    }
      .bodyAsText()
  }

  @OptIn(ExperimentalEncodingApi::class)
  suspend fun getStudentPhoto(uuid: Uuid, token: String): ByteArray {
    return Base64.decode(getBase64EncodedStudentPhoto(uuid, token))
  }
}
