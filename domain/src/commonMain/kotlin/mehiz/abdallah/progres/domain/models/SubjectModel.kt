package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.SubjectDto
import mehiz.abdallah.progres.data.db.SubjectTable

data class SubjectModel(
  val id: Long,
  val oofId: Long,
  val ofStringLatin: String,
  val period: AcademicPeriodModel,
  val levelStringLatin: String,
  val levelStringArabic: String,
  val subjectId: Long,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val subjectCoefficient: Double,
  val subjectCredit: Double,
  val subjectExamCoefficient: Double,
  val subjectCCCoefficient: Double,
  val subjectCICoefficient: Double,
)

fun SubjectDto.toTable(
  yearPeriodCode: String
): SubjectTable {
  return SubjectTable(
    id = id,
    oofId = oofId,
    ofStringLatin = ofLibelleFr,
    levelStringLatin = niveauLibelleFr,
    levelStringArabic = niveauLibelleAr,
    subjectId = rattachementMcId,
    subjectStringLatin = mcLibelleFr,
    subjectStringArabic = mcLibelleAr,
    subjectCoefficient = mcCoefficient,
    subjectCredit = mcCredit,
    subjectCICoefficient = coefficientControleIntermediaire,
    subjectCCCoefficient = coefficientControleContinu,
    subjectExamCoefficient = coefficientExamen,
    yearPeriodCode = yearPeriodCode
  )
}

fun SubjectTable.toModel(
  period: AcademicPeriodModel
): SubjectModel {
  return SubjectModel(
    id = id,
    oofId = oofId,
    ofStringLatin = ofStringLatin,
    period = period,
    levelStringLatin = levelStringLatin,
    levelStringArabic = levelStringArabic,
    subjectId = subjectId,
    subjectStringLatin = subjectStringLatin,
    subjectStringArabic = subjectStringArabic,
    subjectCoefficient = subjectCoefficient,
    subjectCredit = subjectCredit,
    subjectExamCoefficient = subjectExamCoefficient,
    subjectCCCoefficient = subjectCCCoefficient,
    subjectCICoefficient = subjectCICoefficient
  )
}
