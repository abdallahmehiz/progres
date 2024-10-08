package mehiz.abdallah.progres.api

internal const val BASE_URL = "https://progres.mesrs.dz"
internal const val QUITTANCE_URL = "https://quittance.mesrs.dz"

enum class Endpoints(private val endpoint: String, private val baseUrl: String = BASE_URL) {
  Login("/api/authentication/v1/"),
  GetStudentPhoto("/api/infos/image/{uuid}"),
  GetEstablishmentLogo("/api/infos/logoEtablissement/{establishmentId}"),
  GetStudentCards("/api/infos/bac/{uuid}/dias"),
  GetTransportState("/api/infos/demandeTransport/{uuid}/{cardId}"),
  GetAccommodation("/api/infos/bac/{uuid}/demandesHebregement"),
  GetIndividualInfo("/api/infos/bac/{uuid}/individu"),
  GetExamGrades("/api/infos/planningSession/dia/{cardId}/noteExamens"),
  GetExamsSchedule("/api/infos/Examens/{offerId}/niveau/{levelId}/examens"),
  GetGroups("/api/infos/dia/{cardId}/groups"),
  GetSubjects("/api/infos/offreFormation/{offerId}/niveau/{levelId}/Coefficients"),
  GetSubjectSchedule("/api/infos/seanceEmploi/inscription/{cardId}"),
  GetBacInfo("/api/infos/bac/{uuid}"),
  GetBacGrades("/api/infos/bac/{uuid}/notes"),
  GetAcademicTranscripts("/api/infos/bac/{uuid}/dias/{cardId}/periode/bilans"),
  GetAcademicDecision("/api/infos/bac/{uuid}/dia/{cardId}/annuel/bilan"),
  GetCCGrades("/api/infos/controleContinue/dia/{cardId}/notesCC"),
  GetCurrentAcademicYear("/api/infos/AnneeAcademicqueEncours"),
  GetAcademicPeriods("/api/infos/niveau/{yearId}/periodes"),
  GetAcademicVacations("/api/infos/congeacademiques/{uuid}"), // returns 403 on all requests rn
  GetDischarges("/api/{uuid}/qitus", QUITTANCE_URL),
  GetDebts("/api/infos/dettes/{uuid}"),
  ;

  // Wanted to use String.format() but that is only for jvm :pain:
  operator fun invoke(vararg params: Any): String {
    var url = "$baseUrl$endpoint"
    val regex = "\\{[^}]+\\}".toRegex()
    params.forEach { url = url.replaceFirst(regex, it.toString()) }
    return url
  }
}
