package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectDto(
  val coefficientControleContinu: Double,
  val coefficientControleIntermediaire: Double,
  val coefficientExamen: Double,
  val mcLibelleAr: String,
  val mcLibelleFr: String,
  val periodeLibelleAr: String,
  val periodeLibelleFr: String,
)
