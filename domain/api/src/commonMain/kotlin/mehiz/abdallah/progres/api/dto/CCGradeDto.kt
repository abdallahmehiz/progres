package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CCGradeDto(
  val absent: Boolean,
  val apCode: String,
  val autorisationDemandeRecours: Boolean,
  val dateDebutDepotRecours: String? = null,
  val dateLimiteDepotRecours: String? = null,
  val id: Long,
  val llPeriode: String,
  val llPeriodeAr: String,
  val note: Double? = null,
  val observation: String? = null,
  val rattachementMcMcLibelleAr: String,
  val rattachementMcMcLibelleFr: String,
)
