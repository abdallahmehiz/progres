package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.SubjectDto
import mehiz.abdallah.progres.data.db.SubjectTable

data class SubjectModel(
  val id: Long,
  val oofId: Long,
  val ofStringLatin: String,
  val periodId: Long,
  val periodStringLatin: String,
  val periodStringArabic: String,
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

fun SubjectDto.toTable(): SubjectTable {
  return SubjectTable(
    id = id,
    oofId = oofId,
    ofStringLatin = ofLibelleFr,
    periodId = periodeId,
    periodStringLatin = periodeLibelleFr,
    periodStringArabic = periodeLibelleAr,
    levelStringLatin = niveauLibelleFr,
    levelStringArabic = niveauLibelleAr,
    subjectId = rattachementMcId,
    subjectStringLatin = mcLibelleFr,
    subjectStringArabic = mcLibelleAr,
    subjectCoefficient = mcCoefficient,
    subjectCredit = mcCredit,
    subjectCICoefficient = coefficientControleIntermediaire,
    subjectCCCoefficient = coefficientControleContinu,
    subjectExamCoefficient = coefficientExamen
  )
}

fun SubjectTable.toModel(): SubjectModel {
  return SubjectModel(
    id = id,
    oofId = oofId,
    ofStringLatin = ofStringLatin,
    periodId = periodId,
    periodStringLatin = periodStringLatin,
    periodStringArabic = periodStringArabic,
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
