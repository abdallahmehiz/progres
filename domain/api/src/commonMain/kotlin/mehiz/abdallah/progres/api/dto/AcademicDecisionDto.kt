package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AcademicDecisionDto(
  val creditAcquis: Double? = null,
  val moyenne: Double? = null,
  val typeDecisionLibelleAr: String? = null,
  val typeDecisionLibelleFr: String? = null,
)
