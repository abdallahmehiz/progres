package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import mehiz.abdallah.progres.api.dto.ExamScheduleDto
import mehiz.abdallah.progres.data.db.ExamScheduleTable

data class ExamScheduleModel(
  val id: Long,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val sessionTypeLatin: String,
  val sessionTypeArabic: String,
  val periodStringLatin: String,
  val periodStringArabic: String,
  val examDate: LocalDateTime,
  val examStartHour: LocalDateTime,
  val examEndHour: LocalDateTime?,
  val duration: Long,
)

fun ExamScheduleDto.toTable(): ExamScheduleTable {
  return ExamScheduleTable(
    id = id,
    subjectStringLatin = mcLibelleFr,
    subjectStringArabic = mcLibelleAr,
    plannedCoefficientForEliminatoryGrade = planningCoefficientNoteEliminatoire,
    sessionTypeLatin = typeSession,
    sessionTypeArabic = typeSessionAr,
    periodStringLatin = libellePeriode,
    periodStringArabic = libellePeriodeAr,
    examDate = dateExamen,
    examStartHour = heureDebut,
    examEndHour = heureFin,
    duration = duree,
    isAnonymous = anonymat?.let { if (it) 1L else 0L },
    isGradeManagedByTeacher = noteGereParEnseignant?.let { if (it) 1L else 0L },
  )
}

fun ExamScheduleTable.toModel(): ExamScheduleModel {
  return ExamScheduleModel(
    id = id,
    subjectStringArabic = subjectStringArabic,
    subjectStringLatin = subjectStringLatin,
    sessionTypeLatin = sessionTypeLatin,
    sessionTypeArabic = sessionTypeArabic,
    periodStringLatin = periodStringLatin,
    periodStringArabic = periodStringArabic,
    examDate = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(examDate).toLocalDateTime(),
    examStartHour = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(examStartHour).toLocalDateTime(),
    examEndHour = examEndHour?.let {
      DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it).toLocalDateTime()
    },
    duration = duration,
  )
}
