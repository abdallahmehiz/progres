package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptSubjectsDto(
  @SerialName("id_bilan_ue")
  val bilanUeId: Long,
  val coefficient: Double,
  val creditObtenu: Double,
  val mcLibelleAr: String,
  val mcLibelleFr: String,
  val moyenneGenerale: Double? = null,
)
