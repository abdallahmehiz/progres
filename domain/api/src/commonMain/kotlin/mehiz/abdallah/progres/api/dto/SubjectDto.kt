package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectDto(
  val anneeAcademiqueDeuxiemeAnnee: Long,
  val anneeAcademiquePremiereAnnee: Long,
  val coefficientControleContinu: Double,
  val coefficientControleIntermediaire: Double,
  val coefficientExamen: Double,
  val id: Long,
  val mcCoefficient: Double,
  val mcCredit: Double,
  val mcLibelleAr: String,
  val mcLibelleFr: String,
  val niveauLibelleAr: String,
  val niveauLibelleFr: String,
  val ofLibelleFr: String,
  val oofId: Long,
  val periodeId: Long,
  val periodeLibelleAr: String,
  val periodeLibelleFr: String,
  val rattachementMcId: Long,
)
