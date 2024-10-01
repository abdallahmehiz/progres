package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import mehiz.abdallah.progres.api.dto.SubjectScheduleDto
import mehiz.abdallah.progres.data.db.SubjectScheduleTable

data class SubjectScheduleModel(
  val id: Long,
  val ap: String,
  val academicGroupAssociationId: Long,
  val academicGroupId: Long,
  val periodId: Long,
  val period: AcademicPeriodModel?,
  val sectionId: Long?,
  val groupString: String,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val scheduleId: Long,
  val teacherId: Long?,
  val teacherFirstNameLatin: String?,
  val teacherFirstNameArabic: String?,
  val teacherLastNameLatin: String?,
  val teacherLastNameArabic: String?,
  val hourlyRangeId: Long?,
  val hourlyRangeStringLatin: String?,
  val hourlyRangeStart: LocalTime?,
  val hourlyRangeEnd: LocalTime?,
  val day: DayOfWeek?,
  val locationId: String?,
  val locationDesignation: String?,
  val locationTypeLatin: String?,
  val scheduleSegmented: String?
)

fun SubjectScheduleDto.toTable(
  yearPeriodCode: String?,
): SubjectScheduleTable {
  return SubjectScheduleTable(
    id = id,
    ap = ap,
    academicGroupAssosiationId = associationGroupePedagogiqueId,
    academicGroupId = groupePedagogiqueId,
    periodId = periodeId,
    yearPeriodCode = yearPeriodCode,
    sectionId = sectionId,
    groupString = groupe,
    subjectStringLatin = matiere,
    subjectStringArabic = matiereAr,
    scheduleId = emploiId,
    teacherId = enseignantId?.toLong(),
    teacherFirstNameLatin = prenomEnseignantLatin,
    teacherFirstNameArabic = prenomEnseignantArabe,
    teacherLastNameLatin = nomEnseignantLatin,
    teacherLastNameArabic = nomEnseignantArabe,
    hourlyRangeId = plageHoraireId,
    hourlyRangeStringLatin = plageHoraireLibelleFr,
    hourlyRangeStart = plageHoraireHeureDebut,
    hourlyRangeEnd = plageHoraireHeureFin,
    dayId = jourId,
    dayCode = jourCode,
    dayStringLatin = jourLibelleFr,
    dayStringArabic = jourLibelleAr,
    locationId = refLieuId?.toString(),
    locationDesignation = refLieuDesignation,
    locationTypeLatin = refLieuTypeLibelleLongFr,
    scheduleSegmented = libelleSeance
  )
}

fun SubjectScheduleTable.toModel(
  period: AcademicPeriodModel?
): SubjectScheduleModel {
  return SubjectScheduleModel(
    id = id,
    ap = ap,
    academicGroupAssociationId = academicGroupAssosiationId,
    academicGroupId = academicGroupId,
    periodId = periodId,
    sectionId = sectionId,
    groupString = groupString,
    subjectStringLatin = subjectStringLatin,
    subjectStringArabic = subjectStringArabic,
    scheduleId = scheduleId,
    teacherId = teacherId,
    teacherFirstNameLatin = teacherFirstNameLatin,
    teacherFirstNameArabic = teacherFirstNameArabic,
    teacherLastNameLatin = teacherLastNameLatin,
    teacherLastNameArabic = teacherLastNameArabic,
    hourlyRangeId = hourlyRangeId,
    hourlyRangeStringLatin = hourlyRangeStringLatin,
    hourlyRangeStart = hourlyRangeStart?.let { parseTime(it) },
    hourlyRangeEnd = hourlyRangeEnd?.let { parseTime(it) },
    day = dayId?.let { parseDay(it.toInt()) },
    locationId = locationId,
    locationDesignation = locationDesignation,
    locationTypeLatin = locationTypeLatin,
    scheduleSegmented = scheduleSegmented,
    period = period
  )
}

fun parseDay(dayId: Int): DayOfWeek {
  return if (dayId == 1) DayOfWeek.SUNDAY else DayOfWeek(dayId - 1)
}

fun parseTime(timeString: String): LocalTime {
  val (hour, minute) = timeString.split(":").map { it.toInt() }
  return LocalTime(hour, minute)
}
