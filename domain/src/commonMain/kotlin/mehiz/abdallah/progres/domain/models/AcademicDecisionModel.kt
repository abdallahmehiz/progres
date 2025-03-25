package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.AcademicDecisionDto
import mehiz.abdallah.progres.data.db.AcademicDecisionTable

data class AcademicDecisionModel(
  val decisionStringLatin: String?,
  val decisionStringArabic: String?,
  val average: Double?,
  val creditAcquired: Double?,
  val period: AcademicPeriodModel,
)

fun AcademicDecisionDto.toTable(
  yearPeriodCode: String,
): AcademicDecisionTable {
  return AcademicDecisionTable(
    yearPeriodCode = yearPeriodCode,
    decisionStringLatin = typeDecisionLibelleFr,
    decisionStringArabic = typeDecisionLibelleAr,
    average = moyenne,
    creditAcquired = creditAcquis
  )
}

fun AcademicDecisionTable.toModel(
  academicPeriod: AcademicPeriodModel
): AcademicDecisionModel {
  return AcademicDecisionModel(
    decisionStringLatin = decisionStringLatin,
    decisionStringArabic = decisionStringArabic,
    average = average,
    period = academicPeriod,
    creditAcquired = creditAcquired
  )
}
