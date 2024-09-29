package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptUEDto(
  val bilanMcs: List<TranscriptSubjectsDto>,
  val bilanSessionId: Int,
  val coefficient: Double,
  val credit: Double,
  val creditAcquis: Double,
  val creditObtenu: Double,
  val effectif: Int,
  val id: Int,
  val moyenne: Double,
  val repartitionUeId: Int,
  val totalAquis: Int,
  val ueAcquis: Boolean,
  val ueCode: String,
  val ueLibelleFr: String,
  val ueNatureCode: String,
  val ueNatureLcAr: String,
  val ueNatureLcFr: String,
  val ueNoteObtention: Double,
  val ueType: String
)
