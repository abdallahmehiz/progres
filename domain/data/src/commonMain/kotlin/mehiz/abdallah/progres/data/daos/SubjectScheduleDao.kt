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
        academicGroupAssosiationId = academicGroupAssosiationId,
        academicGroupId = academicGroupId,
        periodId = periodId,
        yearPeriodCode = yearPeriodCode,
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
        hourlyRangeStart = hourlyRangeStart,
        hourlyRangeEnd = hourlyRangeEnd,
        dayId = dayId,
        dayCode = dayCode,
        dayStringLatin = dayStringLatin,
        dayStringArabic = dayStringArabic,
        locationId = locationId,
        locationDesignation = locationDesignation,
        locationTypeLatin = locationTypeLatin,
        scheduleSegmented = scheduleSegmented
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
