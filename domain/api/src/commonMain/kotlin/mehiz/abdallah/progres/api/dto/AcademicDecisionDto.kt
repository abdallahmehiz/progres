package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AcademicDecisionDto(
  val annuel: Boolean,
  val coefficient: Double? = null,
  val credit: Double? = null,
  val creditAcquis: Double? = null,
  val creditObtenu: Double? = null,
  val cumulCreditPrecedent: Double? = null,
  val effectif: Long? = null,
  val id: Long,
  val moyenne: Double? = null,
  val moyenneSn: Double? = null,
  val niveauRang: Long,
  val totalAquis: Long,
  val type: Long,
  val typeDecisionLibelleAr: String? = null,
  val typeDecisionLibelleFr: String? = null,
)
