package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptDto(
  val annuel: Boolean,
  val bilanUes: List<TranscriptUEDto>,
  val coefficient: Double? = null,
  val credit: Double? = null,
  val creditAcquis: Double? = null,
  val creditObtenu: Double? = null,
  val cycleLibelleLongLt: String,
  val effectif: Long,
  val id: Long,
  val moyenne: Double? = null,
  val moyenneSn: Double? = null,
  val niveauCode: String,
  val niveauLibelleLongAr: String,
  val niveauLibelleLongLt: String,
  val niveauRang: Long,
  val periodeId: Long,
  val periodeLibelleAr: String,
  val periodeLibelleFr: String,
  val totalAquis: Long,
  val type: Long,
)
