package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.DischargeDto
import mehiz.abdallah.progres.data.db.DischargeTable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DischargeModel(
  val uuid: Uuid,
  val departmentLevel: Boolean,
  val facultyLevel: Boolean,
  val centralLibraryLevel: Boolean,
  val residenceLevel: Boolean,
  val scholarshipLevel: Boolean,
)

fun DischargeDto.toTable(): DischargeTable {
  return DischargeTable(
    uuid = uuid,
    department = departmentLevel,
    residence = residenceLevel,
    faculty = facultyLevel,
    scholarship = scholarshipServiceLevel,
    centralLibrary = centralLibraryLevel
  )
}

@OptIn(ExperimentalUuidApi::class)
fun DischargeTable.toModel(): DischargeModel {
  return DischargeModel(
    uuid = Uuid.parse(uuid),
    departmentLevel = department == 1L,
    facultyLevel = faculty == 1L,
    centralLibraryLevel = centralLibrary == 1L,
    residenceLevel = residence == 1L,
    scholarshipLevel = scholarship == 1L
  )
}
