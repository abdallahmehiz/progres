package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ExamScheduleTable
import mehiz.abdallah.progres.data.db.ProgresDB

class ExamScheduleDao(
  db: ProgresDB
) {
  private val queries = db.examScheduleTableQueries

  fun insert(examSchedule: ExamScheduleTable) {
    with(examSchedule) {
      queries.insert(
        id = id,
        subjectStringLatin = subjectStringLatin,
        subjectStringArabic = subjectStringArabic,
        plannedCoefficientForEliminatoryGrade = plannedCoefficientForEliminatoryGrade,
        sessionTypeLatin = sessionTypeLatin,
        sessionTypeArabic = sessionTypeArabic,
        examDate = examDate,
        examStartHour = examStartHour,
        examEndHour = examEndHour,
        duration = duration,
        isAnonymous = isAnonymous,
        isGradeManagedByTeacher = isGradeManagedByTeacher,
        yearPeriodCode = yearPeriodCode
      )
    }
  }

  fun getAllExamSchedules(): List<ExamScheduleTable> {
    return queries.getAllExamSchedules().executeAsList()
  }

  fun deleteAllExamSchedules() {
    queries.deleteAllExamSchedules()
  }
}
