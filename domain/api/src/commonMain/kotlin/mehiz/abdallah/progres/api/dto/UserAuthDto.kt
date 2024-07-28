package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAuthDto(
  var expirationDate: String,
  var token: String,
  var userId: Long,
  var uuid: String,
  @SerialName("idIndividu")
  var individualId: Long = 0,
  @SerialName("etablissementId")
  var establishmentId: Int,
  var userName: String,
)
