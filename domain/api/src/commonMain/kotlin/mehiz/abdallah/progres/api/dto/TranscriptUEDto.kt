package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptUEDto(
  val bilanMcs: List<TranscriptSubjectsDto>,
  @SerialName("id_bilan_session")
  val bilanSessionId: Long,
  val credit: Double,
  val creditAcquis: Double,
  val moyenne: Double,
  val ueLibelleFr: String,
  val ueLibelleAr: String,
  val ueNatureLcAr: String,
  val ueNatureLcFr: String,
)
