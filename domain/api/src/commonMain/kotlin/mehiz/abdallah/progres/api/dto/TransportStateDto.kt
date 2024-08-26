package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransportStateDto(
  val id: Long,
  val transportPayed: Boolean? = false,
)
