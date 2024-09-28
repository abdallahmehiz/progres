package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class BacGradeDto(
  val note: Double,
  val refCodeMatiereLibelleFr: String,
)
