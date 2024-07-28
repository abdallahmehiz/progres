package mehiz.abdallah.progres.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import mehiz.abdallah.progres.api.dto.UserAuthDto

class ProgresApi(
  private val client: HttpClient,
) {
  suspend fun login(id: String, password: String): UserAuthDto {
    return client.request(
      POST(
        Endpoints.Login.buildUrl(),
        body = """{"username": "$id", "password": "$password"}""",
      ),
    ).body<UserAuthDto>()
  }
}
