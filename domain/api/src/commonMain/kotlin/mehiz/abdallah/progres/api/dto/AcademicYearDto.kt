package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AcademicYearDto(
  val code: String,
  val id: Int,
  val libelle: String? = null,
)
