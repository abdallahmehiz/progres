package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.SubjectDto
import mehiz.abdallah.progres.data.db.SubjectTable
import kotlin.random.Random

data class SubjectModel(
  val period: AcademicPeriodModel,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val subjectExamCoefficient: Double,
  val subjectCCCoefficient: Double,
  val subjectCICoefficient: Double,
)

fun SubjectDto.toTable(
  yearPeriodCode: String
): SubjectTable {
  return SubjectTable(
    Random.nextLong(),
    subjectStringLatin = mcLibelleFr,
    subjectStringArabic = mcLibelleAr,
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
    period = period,
    subjectStringLatin = subjectStringLatin,
    subjectStringArabic = subjectStringArabic,
    subjectExamCoefficient = subjectExamCoefficient,
    subjectCCCoefficient = subjectCCCoefficient,
    subjectCICoefficient = subjectCICoefficient
  )
}
