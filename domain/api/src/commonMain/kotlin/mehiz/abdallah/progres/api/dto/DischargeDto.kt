package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DischargeDto(
  @SerialName("sitBc")
  val centralLibraryLevel: Long,
  @SerialName("sitBf")
  val facultyLevel: Long,
  @SerialName("sitBrs")
  val scholarshipServiceLevel: Long,
  @SerialName("sitDep")
  val departmentLevel: Long,
  @SerialName("sitRu")
  val residenceLevel: Long,
  val uuid: String
)
