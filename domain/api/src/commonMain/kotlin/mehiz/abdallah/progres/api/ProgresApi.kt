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
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import mehiz.abdallah.progres.api.dto.AcademicDecisionDto
import mehiz.abdallah.progres.api.dto.AcademicPeriodDto
import mehiz.abdallah.progres.api.dto.AcademicYearDto
import mehiz.abdallah.progres.api.dto.AccommodationDto
import mehiz.abdallah.progres.api.dto.BacGradeDto
import mehiz.abdallah.progres.api.dto.BacInfoDto
import mehiz.abdallah.progres.api.dto.CCGradeDto
import mehiz.abdallah.progres.api.dto.DischargeDto
import mehiz.abdallah.progres.api.dto.ExamGradeDto
import mehiz.abdallah.progres.api.dto.ExamScheduleDto
import mehiz.abdallah.progres.api.dto.GroupDto
import mehiz.abdallah.progres.api.dto.IndividualInfoDto
import mehiz.abdallah.progres.api.dto.StudentCardDto
import mehiz.abdallah.progres.api.dto.SubjectDto
import mehiz.abdallah.progres.api.dto.SubjectScheduleDto
import mehiz.abdallah.progres.api.dto.TranscriptDto
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
    val request = client.request(
      POST(
        Endpoints.Login(),
        body = """{"username": "$id", "password": "$password"}""",
      ),
    )
    return if (!request.status.isSuccess()) throw Exception(request.bodyAsText()) else request.body()
  }

  private suspend fun getBase64EncodedStudentPhoto(uuid: Uuid, token: String): String? {
    val request = client.get(
      Endpoints.GetStudentPhoto(uuid),
    ) {
      headers {
        appendAll(bearerToken(token))
        append(HttpHeaders.Accept, "image/png")
      }
    }
    return if (request.status.isSuccess()) request.bodyAsText() else null
  }

  @OptIn(ExperimentalEncodingApi::class)
  suspend fun getStudentPhoto(uuid: Uuid, token: String): ByteArray? {
    return getBase64EncodedStudentPhoto(uuid, token)?.let { Base64.decode(it) }
  }

  private suspend fun getBase64EncodedEstablishmentLogo(establishmentId: Long, token: String): String? {
    val request = client.get(
      Endpoints.GetEstablishmentLogo(establishmentId),
    ) {
      headers {
        appendAll(bearerToken(token))
        append(HttpHeaders.Accept, "image/png")
      }
    }
    return if (request.status.isSuccess()) request.bodyAsText() else null
  }

  @OptIn(ExperimentalEncodingApi::class)
  suspend fun getEstablishmentLogo(establishmentId: Long, token: String): ByteArray? {
    return getBase64EncodedEstablishmentLogo(establishmentId, token)?.let { Base64.decode(it) }
  }

  suspend fun getStudentCards(uuid: Uuid, token: String): List<StudentCardDto> {
    return client.request(GET(Endpoints.GetStudentCards(uuid), bearerToken(token)))
      .body()
  }

  // for some reason instead of returning a dto with a value being false, they decided to return an empty response...
  suspend fun getTransportState(uuid: Uuid, cardId: Long, token: String): TransportStateDto? {
    val request = client.request(GET(Endpoints.GetTransportState(uuid, cardId), bearerToken(token)))
    val body = request.bodyAsText()
    return if (body.isBlank()) null else json.decodeFromString(body)
  }

  suspend fun getIndividualInfo(uuid: Uuid, token: String): IndividualInfoDto {
    return client.request(GET(Endpoints.GetIndividualInfo(uuid), bearerToken(token)))
      .body()
  }

  suspend fun getExamGrades(cardId: Long, token: String): List<ExamGradeDto> {
    val request = client.request(GET(Endpoints.GetExamGrades(cardId), bearerToken(token)))
    val body = request.bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getExamsScheduleForPeriod(periodId: Long, levelId: Long, token: String): List<ExamScheduleDto> {
    return client.request(GET(Endpoints.GetExamsSchedule(periodId, levelId), bearerToken(token)))
      .body()
  }

  suspend fun getGroups(cardId: Long, token: String): List<GroupDto> {
    val body = client.request(GET(Endpoints.GetGroups(cardId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getSubjects(offerId: Long, levelId: Long, token: String): List<SubjectDto> {
    val body = client.request(GET(Endpoints.GetSubjects(offerId, levelId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getSubjectsSchedule(cardId: Long, token: String): List<SubjectScheduleDto> {
    val body = client.request(GET(Endpoints.GetSubjectSchedule(cardId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getBacInfo(uuid: Uuid, token: String): BacInfoDto {
    return client.request(GET(Endpoints.GetBacInfo(uuid), bearerToken(token))).body()
  }

  suspend fun getBacNotes(uuid: Uuid, token: String): List<BacGradeDto> {
    val body = client.request(GET(Endpoints.GetBacGrades(uuid), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getAcademicDecision(uuid: Uuid, cardId: Long, token: String): AcademicDecisionDto? {
    val body = client.request(GET(Endpoints.GetAcademicDecision(uuid, cardId), bearerToken(token)))
      .bodyAsText()
    return if (body.isEmpty()) null else json.decodeFromString<List<AcademicDecisionDto>>(body)[0]
  }

  suspend fun getAcademicTranscripts(uuid: Uuid, cardId: Long, token: String): List<TranscriptDto> {
    val body = client.request(GET(Endpoints.GetAcademicTranscripts(uuid, cardId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getCCGrades(cardId: Long, token: String): List<CCGradeDto> {
    val body = client.request(GET(Endpoints.GetCCGrades(cardId), bearerToken(token)))
      .bodyAsText()
    return if (body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getCurrentAcademicYear(token: String): AcademicYearDto {
    return client.request(GET(Endpoints.GetCurrentAcademicYear(), bearerToken(token))).body()
  }

  suspend fun getAcademicPeriods(year: Long, token: String): List<AcademicPeriodDto> {
    return client.request(GET(Endpoints.GetAcademicPeriods(year), bearerToken(token))).body()
  }

  suspend fun getAccommodationStates(uuid: Uuid, token: String): List<AccommodationDto> {
    val request = client.request(GET(Endpoints.GetAccommodation(uuid), bearerToken(token)))
    val body = request.bodyAsText()
    return if (!request.status.isSuccess() || body.isBlank()) emptyList() else json.decodeFromString(body)
  }

  suspend fun getDischargeState(uuid: Uuid): DischargeDto? {
    val body = client.request(GET(Endpoints.GetDischarges(uuid))).body<List<DischargeDto>>()
    return if (body.isEmpty()) null else body[0]
  }
}
