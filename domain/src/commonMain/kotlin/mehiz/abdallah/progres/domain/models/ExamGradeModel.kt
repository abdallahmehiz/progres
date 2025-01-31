package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import mehiz.abdallah.progres.api.dto.ExamGradeDto
import mehiz.abdallah.progres.data.db.ExamGradeTable

data class ExamGradeModel(
  val id: Long,
  val ueCode: String,
  val ueNatureLatin: String,
  val subjectLabelLatin: String,
  val subjectLabelArabic: String,
  val grade: Double?,
  val coefficent: Double,
  val credit: Double,
  val sessionTitle: String,
  val appealStartDate: LocalDateTime?,
  val appealLimitDate: LocalDateTime?,
  val isAppealAuthorized: Boolean,
  val period: AcademicPeriodModel,
)

fun ExamGradeDto.toTable(
  yearPeriodCode: String
): ExamGradeTable {
  return ExamGradeTable(
    id = id,
    mcId = rattachementMcId,
    ueCode = ueCode ?: "N/A",
    ueNatureLatin = ueNatureLlFr ?: "N/A",
    mcCredit = rattachementMcCredit ?: 0.0,
    mcCoefficent = rattachementMcCoefficient,
    periodId = idPeriode,
    grade = noteExamen,
    planningSessionTitle = planningSessionIntitule,
    planningSessionId = planningSessionId,
    isAppealAuthorized = if (autorisationDemandeRecours) 1L else 0L,
    appealLimitDate = dateLimiteDepotRecours,
    appealStartDate = dateDebutDepotRecours,
    subjectLabelLatin = mcLibelleFr,
    subjectLabelArabic = mcLibelleAr,
    yearPeriodCode = yearPeriodCode
  )
}

fun ExamGradeTable.toModel(
  period: AcademicPeriodModel
): ExamGradeModel {
  return ExamGradeModel(
    id = id,
    ueCode = ueCode,
    ueNatureLatin = ueNatureLatin,
    subjectLabelLatin = subjectLabelLatin,
    subjectLabelArabic = subjectLabelArabic,
    coefficent = mcCoefficent,
    credit = mcCredit,
    appealLimitDate = appealLimitDate?.let(LocalDateTime::parse),
    appealStartDate = appealStartDate?.let(LocalDateTime::parse),
    isAppealAuthorized = isAppealAuthorized == 1L,
    grade = grade,
    sessionTitle = planningSessionTitle,
    period = period
  )
}
