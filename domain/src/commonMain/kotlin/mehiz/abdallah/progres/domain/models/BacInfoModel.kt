@file:OptIn(ExperimentalUuidApi::class)

package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import mehiz.abdallah.progres.api.dto.BacGradeDto
import mehiz.abdallah.progres.api.dto.BacInfoDto
import mehiz.abdallah.progres.data.db.BacGradeTable
import mehiz.abdallah.progres.data.db.BacInfoTable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class BacGradeModel(
  val subjectName: String,
  val grade: Double,
)

data class BacInfoModel(
  val id: String,
  val uuid: Uuid,
  val nationalID: String,
  val registrationNumber: String,
  val firstNameLatin: String,
  val firstNameArabic: String,
  val lastNameLatin: String,
  val lastNameArabic: String,
  val dateOfBirth: LocalDateTime,
  val stateNumber: Int,
  val seriesCode: String,
  val seriesString: String?,
  val bacYear: Int,
  val grade: Double,
  val grades: List<BacGradeModel>
)

fun BacInfoDto.toTable(): BacInfoTable {
  return BacInfoTable(
    id = id.toString(),
    uuid = uuid,
    nationalID = nin,
    registrationNumber = matricule,
    firstNameLatin = nomFr,
    firstNameArabic = nomAr,
    lastNameLatin = prenomFr,
    lastNameArabic = prenomAr,
    birthDate = dateNaissance,
    stateCode = refCodeWilayaBac.toLong(),
    seriesCode = refCodeSerieBac,
    seriesStringLatin = libelleSerieBac,
    bacYear = anneeBac.toLong(),
    grade = moyenneBac.toDouble()
  )
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun BacInfoTable.toModel(grades: List<BacGradeModel>): BacInfoModel {
  return BacInfoModel(
    id = id,
    uuid = Uuid.parse(uuid),
    nationalID = nationalID,
    registrationNumber = registrationNumber,
    firstNameLatin = firstNameLatin,
    firstNameArabic = firstNameArabic,
    lastNameLatin = lastNameLatin,
    lastNameArabic = lastNameArabic,
    dateOfBirth = LocalDateTime.parse(birthDate.substringBeforeLast('+')),
    stateNumber = stateCode.toInt(),
    seriesCode = seriesCode,
    seriesString = seriesStringLatin,
    bacYear = bacYear.toInt(),
    grade = grade,
    grades = grades
  )
}

fun BacGradeDto.toTable(): BacGradeTable? {
  return note?.let { BacGradeTable(subjectName = refCodeMatiereLibelleFr, grade = it) }
}

fun BacGradeTable.toModel(): BacGradeModel {
  return BacGradeModel(subjectName = subjectName, grade = grade)
}
