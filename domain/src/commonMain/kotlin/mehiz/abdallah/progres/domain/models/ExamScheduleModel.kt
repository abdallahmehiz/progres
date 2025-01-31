package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import mehiz.abdallah.progres.api.dto.ExamScheduleDto
import mehiz.abdallah.progres.data.db.ExamScheduleTable
import kotlin.random.Random

data class ExamScheduleModel(
  val id: Long,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val sessionTypeLatin: String,
  val sessionTypeArabic: String,
  val examDate: LocalDate,
  val examStartHour: LocalTime,
  val examEndHour: LocalTime?,
  val duration: Long,
  val period: AcademicPeriodModel
)

fun ExamScheduleDto.toTable(
  yearPeriodCode: String
): ExamScheduleTable {
  return ExamScheduleTable(
    id = id ?: Random.nextLong(),
    subjectStringLatin = mcLibelleFr,
    subjectStringArabic = mcLibelleAr,
    plannedCoefficientForEliminatoryGrade = planningCoefficientNoteEliminatoire ?: 0.0,
    sessionTypeLatin = typeSession,
    sessionTypeArabic = typeSessionAr ?: typeSession,
    examDate = dateExamen,
    examStartHour = heureDebut,
    examEndHour = heureFin,
    duration = duree,
    isAnonymous = anonymat?.let { if (it) 1L else 0L },
    isGradeManagedByTeacher = noteGereParEnseignant?.let { if (it) 1L else 0L },
    yearPeriodCode = yearPeriodCode
  )
}

fun ExamScheduleTable.toModel(
  period: AcademicPeriodModel,
): ExamScheduleModel {
  return ExamScheduleModel(
    id = id,
    subjectStringArabic = subjectStringArabic,
    subjectStringLatin = subjectStringLatin,
    sessionTypeLatin = sessionTypeLatin,
    sessionTypeArabic = sessionTypeArabic,
    examDate = LocalDate.parse(examDate),
    examStartHour = LocalTime.parse(examStartHour),
    examEndHour = examEndHour?.let(LocalTime::parse),
    duration = duration,
    period = period
  )
}
