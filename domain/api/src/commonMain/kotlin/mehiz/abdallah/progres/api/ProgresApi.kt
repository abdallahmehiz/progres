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
import kotlinx.serialization.json.Json
import mehiz.abdallah.progres.api.dto.BacGradeDto
import mehiz.abdallah.progres.api.dto.BacInfoDto
import mehiz.abdallah.progres.api.dto.ExamGradeDto
import mehiz.abdallah.progres.api.dto.ExamScheduleDto
import mehiz.abdallah.progres.api.dto.GroupDto
import mehiz.abdallah.progres.api.dto.IndividualInfoDto
import mehiz.abdallah.progres.api.dto.StudentCardDto
import mehiz.abdallah.progres.api.dto.SubjectDto
import mehiz.abdallah.progres.api.dto.SubjectScheduleDto
import mehiz.abdallah.progres.api.dto.TransportStateDto
import mehiz.abdallah.progres.api.dto.UserAuthDto
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("TooManyFunctions")
class ProgresApi(
  private val client: HttpClient,
  private val json: Json,
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
      Endpoints.GetStudentPhoto.buildUrl(uuid),
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

  private suspend fun getBase64EncodedEstablishmentLogo(establishmentId: Long, token: String): String {
    return client.get(
      Endpoints.GetEstablishmentLogo.buildUrl(establishmentId)
    ) {
      headers {
        appendAll(bearerToken(token))
        append(HttpHeaders.Accept, "image/png")
      }
    }.bodyAsText()
  }

  @OptIn(ExperimentalEncodingApi::class)
  suspend fun getEstablishmentLogo(establishmentId: Long, token: String): ByteArray {
    return Base64.decode(getBase64EncodedEstablishmentLogo(establishmentId, token))
  }

  suspend fun getStudentCards(uuid: Uuid, token: String): List<StudentCardDto> {
    return client.request(GET(Endpoints.GetStudentCards.buildUrl(uuid), bearerToken(token)))
      .body()
  }

  // for some reason instead of returning a dto with a value being false, they decided to return an empty response...
  suspend fun getTransportState(uuid: Uuid, cardId: Long, token: String): TransportStateDto? {
    val request = client.request(GET(Endpoints.TransportState.buildUrl(uuid, cardId), bearerToken(token)))
    val body = request.bodyAsText()
    return if (body.isBlank()) null else json.decodeFromString(body)
  }

  suspend fun getIndividualInfo(uuid: Uuid, token: String): IndividualInfoDto {
    return client.request(GET(Endpoints.GetIndividualInfo.buildUrl(uuid), bearerToken(token)))
      .body()
  }

  suspend fun getExamGrades(cardId: Long, token: String): List<ExamGradeDto> {
    val request = client.request(GET(Endpoints.GetExamGrades.buildUrl(cardId), bearerToken(token)))
    val body = request.bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getExamsScheduleForPeriod(periodId: Long, levelId: Long, token: String): List<ExamScheduleDto> {
    return client.request(GET(Endpoints.GetExamsSchedule.buildUrl(periodId, levelId), bearerToken(token)))
      .body()
  }

  suspend fun getGroups(cardId: Long, token: String): List<GroupDto> {
    val body = client.request(GET(Endpoints.GetGroups.buildUrl(cardId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getSubjects(offerId: Long, levelId: Long, token: String): List<SubjectDto> {
    val body = client.request(GET(Endpoints.GetSubjects.buildUrl(offerId, levelId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getSubjectsSchedule(cardId: Long, token: String): List<SubjectScheduleDto> {
    val body = client.request(GET(Endpoints.GetSubjectSchedule.buildUrl(cardId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getBacInfo(uuid: Uuid, token: String): BacInfoDto {
    return client.request(GET(Endpoints.GetBacInfo.buildUrl(uuid), bearerToken(token))).body()
  }

  suspend fun getBacNotes(uuid: Uuid, token: String): List<BacGradeDto> {
    val body = client.request(GET(Endpoints.GetBacGrades.buildUrl(uuid), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }
}
