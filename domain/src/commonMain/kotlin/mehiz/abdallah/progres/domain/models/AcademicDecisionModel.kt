package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.AcademicDecisionDto
import mehiz.abdallah.progres.data.db.AcademicDecisionTable

data class AcademicDecisionModel(
  val id: Long,
  val type: Long,
  val decisionStringLatin: String?,
  val decisionStringArabic: String?,
  val average: Double?,
  val averageSn: Double?,
  val credit: Double?,
  val creditObtained: Double?,
  val creditAcquired: Double?,
  val cumulatedCredit: Double?,
  val isAnnual: Boolean,
  val period: AcademicPeriodModel,
)

fun AcademicDecisionDto.toTable(
  yearPeriodCode: String,
): AcademicDecisionTable {
  return AcademicDecisionTable(
    id = id,
    yearPeriodCode = yearPeriodCode,
    type = type,
    decisionStringLatin = typeDecisionLibelleFr,
    decisionStringArabic = typeDecisionLibelleAr,
    average = moyenne,
    avearageSn = moyenneSn,
    credit = credit,
    creditObtained = creditObtenu,
    creditAcquired = creditAcquis,
    cumulatedCredit = cumulCreditPrecedent,
    isAnnual = if (annuel) 1 else 0
  )
}

fun AcademicDecisionTable.toModel(
  academicPeriod: AcademicPeriodModel
): AcademicDecisionModel {
  return AcademicDecisionModel(
    id = id,
    type = type,
    decisionStringLatin = decisionStringLatin,
    decisionStringArabic = decisionStringArabic,
    average = average,
    averageSn = avearageSn,
    credit = credit,
    creditObtained = creditObtained,
    creditAcquired = creditAcquired,
    cumulatedCredit = cumulatedCredit,
    isAnnual = isAnnual != 0L,
    period = academicPeriod
  )
}
