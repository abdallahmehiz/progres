package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExamGradeDto(
  val autorisationDemandeRecours: Boolean,
  val dateDebutDepotRecours: String? = null,
  val dateLimiteDepotRecours: String? = null,
  val id: Long,
  val idPeriode: Long,
  val mcLibelleAr: String,
  val mcLibelleFr: String,
  val noteExamen: Double? = null,
  val planningSessionId: Long,
  val planningSessionIntitule: String,
  val rattachementMcCoefficient: Double,
  val rattachementMcCredit: Double,
  val rattachementMcId: Long,
  val ueCode: String,
  val ueNatureLlFr: String,
)
