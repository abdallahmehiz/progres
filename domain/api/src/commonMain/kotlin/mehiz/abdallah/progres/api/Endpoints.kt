package mehiz.abdallah.progres.api

internal const val BASE_URL = "https://progres.mesrs.dz"

enum class Endpoints(private val endpoint: String) {
  Login("/api/authentication/v1/"),
  GetStudentPhoto("/api/infos/image/{uuid}"),
  GetEstablishmentLogo("/api/infos/logoEtablissement/{establishmentId}"),
  GetStudentCards("/api/infos/bac/{uuid}/dias"),
  TransportState("/api/infos/demandeTransport/{uuid}/{cardId}"),
  GetIndividualInfo("/api/infos/bac/{uuid}/individu"),
  GetExamGrades("/api/infos/planningSession/dia/{cardId}/noteExamens"),
  GetExamsSchedule("/api/infos/Examens/{offerId}/niveau/{levelId}/examens")
  ;

  // Wanted to use String.format() but that is only for jvm :pain:
  fun buildUrl(vararg params: Any): String {
    var url = "$BASE_URL$endpoint"
    val regex = "\\{[^}]+\\}".toRegex()
    params.forEach { url = url.replaceFirst(regex, it.toString()) }
    return url
  }
}
