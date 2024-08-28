package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
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
  val periodId: Long,
)

fun ExamGradeDto.toTable(): ExamGradeTable {
  return ExamGradeTable(
    id = id,
    mcId = rattachementMcId,
    ueCode = ueCode,
    ueNatureLatin = ueNatureLlFr,
    mcCredit = rattachementMcCredit,
    mcCoefficent = rattachementMcCoefficient,
    periodId = idPeriode,
    grade = noteExamen,
    planningSessionTitle = planningSessionIntitule,
    planningSessionId = planningSessionId,
    isAppealAuthorized = if (autorisationDemandeRecours) 1L else 0L,
    appealLimitDate = dateLimiteDepotRecours,
    appealStartDate = dateDebutDepotRecours,
    subjectLabelLatin = mcLibelleFr,
    subjectLabelArabic = mcLibelleAr
  )
}

fun ExamGradeTable.toModel(): ExamGradeModel {
  return ExamGradeModel(
    id = id,
    ueCode = ueCode,
    ueNatureLatin = ueNatureLatin,
    subjectLabelLatin = subjectLabelLatin,
    subjectLabelArabic = subjectLabelArabic,
    coefficent = mcCoefficent,
    credit = mcCredit,
    appealLimitDate = appealLimitDate?.let {
      DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it).toLocalDateTime()
    },
    appealStartDate = appealStartDate?.let {
      DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it).toLocalDateTime()
    },
    isAppealAuthorized = isAppealAuthorized == 1L,
    grade = grade,
    sessionTitle = planningSessionTitle,
    periodId = periodId
  )
}
