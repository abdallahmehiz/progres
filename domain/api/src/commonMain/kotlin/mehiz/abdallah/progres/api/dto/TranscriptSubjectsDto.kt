package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptSubjectsDto(
  val bilanSessionId: Int,
  val bilanUeId: Int,
  val coefficient: Double,
  val credit: Double,
  val creditObtenu: Double,
  val id: Int,
  val mcCode: String,
  val mcLibelleAr: String,
  val mcLibelleFr: String,
  val moyenneGenerale: Double? = null,
  val rattachementMcId: Int,
)
