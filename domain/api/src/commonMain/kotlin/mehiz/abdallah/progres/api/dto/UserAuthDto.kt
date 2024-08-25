package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mehiz.abdallah.progres.api.serializers.UuidSerializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class UserAuthDto(
  var expirationDate: String,
  var token: String,
  var userId: Long,
  @Serializable(with = UuidSerializer::class)
  var uuid: Uuid,
  @SerialName("idIndividu")
  var individualId: Long = 0,
  @SerialName("etablissementId")
  var establishmentId: Long,
  var userName: String,
)
