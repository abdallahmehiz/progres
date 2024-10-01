package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import mehiz.abdallah.progres.api.dto.CCGradeDto
import mehiz.abdallah.progres.data.db.CCGradeTable

data class CCGradeModel(
  val id: Long,
  val grade: Double?,
  val wasAbsent: Boolean,
  val observation: String?,
  val period: AcademicPeriodModel,
  val ap: String,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val appealEligibilityDateStart: LocalDateTime?,
  val appealEligibilityDateEnd: LocalDateTime?,
  val appealable: Boolean
)

fun CCGradeDto.toTable(
  yearPeriodCode: String
): CCGradeTable {
  return CCGradeTable(
    id = id,
    grade = note,
    wasAbsent = if (absent) 1L else 0L,
    observation = observation,
    yearPeriodCode = yearPeriodCode,
    ap = apCode,
    subjectStringLatin = rattachementMcMcLibelleFr,
    subjectStringArabic = rattachementMcMcLibelleAr,
    appealStartDate = dateDebutDepotRecours,
    appealEndDate = dateLimiteDepotRecours,
    appealable = if (autorisationDemandeRecours) 1L else 0L
  )
}

fun CCGradeTable.toModel(period: AcademicPeriodModel): CCGradeModel {
  return CCGradeModel(
    id = id,
    grade = grade,
    wasAbsent = wasAbsent == 1L,
    observation = observation,
    period = period,
    ap = ap,
    subjectStringLatin = subjectStringLatin,
    subjectStringArabic = subjectStringArabic,
    appealEligibilityDateStart = appealStartDate?.let {
      DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it).toLocalDateTime()
    },
    appealEligibilityDateEnd = appealEndDate?.let {
      DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it).toLocalDateTime()
    },
    appealable = appealable == 1L
  )
}
