package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.GroupDto
import mehiz.abdallah.progres.data.db.GroupTable

data class GroupModel(
  val id: Long,
  val groupName: String,
  val sectionName: String,
  val periodId: Long,
  val periodStringLatin: String,
)

fun GroupDto.toTable(): GroupTable {
  return GroupTable(
    id = id,
    academicGroupStringLatin = nomGroupePedagogique,
    academicSectionStringLatin = nomSection!!,
    periodId = periodeId,
    periodStringLatin = periodeLibelleLongLt
  )
}

fun GroupTable.toModel(): GroupModel {
  return GroupModel(
    id = id,
    groupName = academicGroupStringLatin,
    sectionName = academicSectionStringLatin,
    periodId = periodId,
    periodStringLatin = periodStringLatin
  )
}
