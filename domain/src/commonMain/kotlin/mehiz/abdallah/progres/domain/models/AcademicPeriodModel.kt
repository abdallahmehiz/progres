package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.data.db.AcademicPeriodTable

data class AcademicPeriodModel(
  val id: Long,
  val periodStringLatin: String,
  val periodStringArabic: String,
  val oofId: Long,
  val academicYearId: Long,
  val academicYearStringLatin: String,
  val academicYearStringArabic: String,
)

fun AcademicPeriodTable.toModel(): AcademicPeriodModel {
  return AcademicPeriodModel(
    id = id,
    periodStringLatin = periodStringLatin,
    periodStringArabic = periodStringArabic,
    oofId = oofId,
    academicYearId = academicYearId,
    academicYearStringLatin = academicYearStringLatin,
    academicYearStringArabic = academicYearStringArabic
  )
}
