package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptDto(
  val bilanUes: List<TranscriptUEDto>,
  val coefficient: Double? = null,
  val credit: Double? = null,
  val creditAcquis: Double? = null,
  val creditObtenu: Double? = null,
  val id: Long,
  val moyenne: Double? = null,
  val moyenneSn: Double? = null,
  val niveauLibelleLongAr: String,
  val niveauLibelleLongLt: String,
  val periodeLibelleAr: String,
  val periodeLibelleFr: String,
)
