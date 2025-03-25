package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.SubjectScheduleTable

class SubjectScheduleDao(
  db: ProgresDB
) {
  private val queries = db.subjectScheduleTableQueries

  fun insert(schedule: SubjectScheduleTable) {
    with(schedule) {
      queries.insert(
        id = id,
        ap = ap,
        periodId = periodId,
        yearPeriodCode = yearPeriodCode,
        groupString = groupString,
        subjectStringLatin = subjectStringLatin,
        subjectStringArabic = subjectStringArabic,
        teacherFirstNameLatin = teacherFirstNameLatin,
        teacherFirstNameArabic = teacherFirstNameArabic,
        teacherLastNameLatin = teacherLastNameLatin,
        teacherLastNameArabic = teacherLastNameArabic,
        hourlyRangeStringLatin = hourlyRangeStringLatin,
        hourlyRangeStart = hourlyRangeStart,
        hourlyRangeEnd = hourlyRangeEnd,
        dayId = dayId,
        dayStringLatin = dayStringLatin,
        dayStringArabic = dayStringArabic,
        locationDesignation = locationDesignation,
      )
    }
  }

  fun getAllSchedules(): List<SubjectScheduleTable> {
    return queries.getAllSubjectSchedules().executeAsList()
  }

  fun deleteAllSchedules() {
    queries.deleteAllSubjectSchedules()
  }
}
