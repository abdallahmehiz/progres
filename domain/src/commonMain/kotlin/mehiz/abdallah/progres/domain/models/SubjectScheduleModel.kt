package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import mehiz.abdallah.progres.api.dto.SubjectScheduleDto
import mehiz.abdallah.progres.data.db.SubjectScheduleTable
import kotlin.random.Random

data class SubjectScheduleModel(
  val id: Long,
  val ap: String,
  val period: AcademicPeriodModel?,
  val groupString: String,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val teacherFirstNameLatin: String?,
  val teacherFirstNameArabic: String?,
  val teacherLastNameLatin: String?,
  val teacherLastNameArabic: String?,
  val hourlyRangeStringLatin: String?,
  val hourlyRangeStart: LocalTime?,
  val hourlyRangeEnd: LocalTime?,
  val day: DayOfWeek?,
  val locationDesignation: String?,
)

fun SubjectScheduleDto.toTable(
  yearPeriodCode: String?,
): SubjectScheduleTable {
  return SubjectScheduleTable(
    id = Random.nextLong(),
    ap = ap,
    periodId = periodeId,
    yearPeriodCode = yearPeriodCode,
    groupString = groupe,
    subjectStringLatin = matiere,
    subjectStringArabic = matiereAr,
    teacherFirstNameLatin = prenomEnseignantLatin,
    teacherFirstNameArabic = prenomEnseignantArabe,
    teacherLastNameLatin = nomEnseignantLatin,
    teacherLastNameArabic = nomEnseignantArabe,
    hourlyRangeStringLatin = plageHoraireLibelleFr,
    hourlyRangeStart = plageHoraireHeureDebut,
    hourlyRangeEnd = plageHoraireHeureFin,
    dayId = jourId,
    dayStringLatin = jourLibelleFr,
    dayStringArabic = jourLibelleAr,
    locationDesignation = refLieuDesignation,
  )
}

fun SubjectScheduleTable.toModel(
  period: AcademicPeriodModel?
): SubjectScheduleModel {
  return SubjectScheduleModel(
    id = id,
    ap = ap,
    groupString = groupString,
    subjectStringLatin = subjectStringLatin,
    subjectStringArabic = subjectStringArabic,
    teacherFirstNameLatin = teacherFirstNameLatin,
    teacherFirstNameArabic = teacherFirstNameArabic,
    teacherLastNameLatin = teacherLastNameLatin,
    teacherLastNameArabic = teacherLastNameArabic,
    hourlyRangeStringLatin = hourlyRangeStringLatin,
    hourlyRangeStart = hourlyRangeStart?.let { parseTime(it) },
    hourlyRangeEnd = hourlyRangeEnd?.let { parseTime(it) },
    day = dayId?.let { parseDay(it.toInt()) },
    locationDesignation = locationDesignation,
    period = period
  )
}

inline fun parseDay(dayId: Int): DayOfWeek {
  return if (dayId == 1) DayOfWeek.SUNDAY else DayOfWeek(dayId - 1)
}

inline fun parseTime(timeString: String): LocalTime {
  val (hour, minute) = timeString.split(":").map { it.toInt() }
  return LocalTime(hour, minute)
}
