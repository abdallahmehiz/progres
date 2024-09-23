package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
  val dateAffectation: String,
  val groupePedagogiqueId: Long,
  val id: Long,
  val nomGroupePedagogique: String,
  val nomSection: String? = null,
  val periodeCode: String,
  val periodeId: Long,
  val periodeLibelleLongLt: String,
)
