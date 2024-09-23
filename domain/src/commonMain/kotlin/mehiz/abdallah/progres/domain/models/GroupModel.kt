package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import mehiz.abdallah.progres.api.dto.GroupDto
import mehiz.abdallah.progres.data.db.GroupTable

data class GroupModel(
  val id: Long,
  val groupId: Long,
  val groupName: String,
  val sectionName: String,
  val assignmentDate: LocalDateTime,
  val periodId: Long,
  val periodCode: String,
  val periodStringLatin: String,
)

fun GroupDto.toTable(): GroupTable {
  return GroupTable(
    id = id,
    academicGroupStringLatin = nomGroupePedagogique,
    academicGroupId = groupePedagogiqueId,
    academicSectionStringLatin = nomSection!!,
    assignmentDate = dateAffectation,
    periodId = periodeId,
    periodCode = periodeCode,
    periodStringLatin = periodeLibelleLongLt
  )
}

fun GroupTable.toModel(): GroupModel {
  return GroupModel(
    id = id,
    groupId = academicGroupId,
    groupName = academicGroupStringLatin,
    sectionName = academicSectionStringLatin,
    assignmentDate = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(assignmentDate).toLocalDateTime(),
    periodId = periodId,
    periodCode = periodCode,
    periodStringLatin = periodStringLatin
  )
}
