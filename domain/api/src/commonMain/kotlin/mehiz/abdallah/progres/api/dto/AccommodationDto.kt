package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccommodationDto(
  val idAnneeAcademique: Long,
  val idDou: Long,
  val llAffectation: String? = null,
  val llDouArabe: String,
  val llDouLatin: String,
  val llResidanceLatin: String,
  val llResidanceArabe: String,
)
