package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
  val id: Long,
  val nomGroupePedagogique: String,
  val nomSection: String? = null,
  val periodeId: Long,
  val periodeLibelleLongLt: String,
)
