package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.AcademicPeriodDto
import mehiz.abdallah.progres.data.db.AcademicPeriodTable

data class AcademicPeriodModel(
  val id: Long,
  val periodCode: String,
  val periodStringLatin: String,
  val periodStringArabic: String,
  val level: Long,
  val levelString: String,
  val cycleString: String,
  val oofId: Long,
  val academicYearId: Long,
  val academicYearCode: String,
  val academicYearStringLatin: String,
  val academicYearStringArabic: String,
  val yearPeriodCode: String
)

fun AcademicPeriodDto.toTable(
  oofId: Long,
  academicYearId: Long,
  academicYearCode: String,
  academicYearStringLatin: String,
  academicYearStringArabic: String
): AcademicPeriodTable {
  return AcademicPeriodTable(
    id = id,
    periodCode = code,
    periodStringLatin = libelleLongLt,
    periodStringArabic = libelleLongAr,
    level = rangNiveau,
    levelString = libelleLongFrNiveau,
    cycleString = libelleLongFrCycle,
    oofId = oofId,
    academicYearId = academicYearId,
    academicYearCode = academicYearCode,
    academicYearStringLatin = academicYearStringLatin,
    academicYearStringArabic = academicYearStringArabic,
    yearPeriodCode = "$academicYearCode - $id"
  )
}

fun AcademicPeriodTable.toModel(): AcademicPeriodModel {
  return AcademicPeriodModel(
    id = id,
    periodCode = periodCode,
    periodStringLatin = periodStringLatin,
    periodStringArabic = periodStringArabic,
    level = level,
    levelString = levelString,
    cycleString = cycleString,
    oofId = oofId,
    academicYearId = academicYearId,
    academicYearCode = academicYearCode,
    academicYearStringLatin = academicYearStringLatin,
    academicYearStringArabic = academicYearStringArabic,
    yearPeriodCode = yearPeriodCode
  )
}
