package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ExamGradeTable
import mehiz.abdallah.progres.data.db.ProgresDB

class ExamGradesDao(
  db: ProgresDB,
) {
  val queries = db.examNoteTableQueries

  fun insert(examGrade: ExamGradeTable) {
    with(examGrade) {
      queries.insert(
        id = id,
        mcId = mcId,
        mcCoefficent = mcCoefficent,
        mcCredit = mcCredit,
        grade = grade,
        ueCode = ueCode,
        ueNatureLatin = ueNatureLatin,
        periodId = periodId,
        appealStartDate = appealStartDate,
        appealLimitDate = appealLimitDate,
        isAppealAuthorized = isAppealAuthorized,
        subjectLabelArabic = subjectLabelArabic,
        subjectLabelLatin = subjectLabelLatin,
        planningSessionTitle = planningSessionTitle,
        planningSessionId = planningSessionId
      )
    }
  }

  fun getAllExamGrades(): List<ExamGradeTable> {
    return queries.getAllExamGrades().executeAsList()
  }

  fun deleteAllExamGrades() {
    queries.deleteAllExamNotes()
  }

  fun getAvailablePeriods(): List<Long> {
    return queries.getAvailablePeriods().executeAsList()
  }

  fun getExamsOfPeriod(periodId: Long): List<ExamGradeTable> {
    return queries.getExamNotesOfPeriod(periodId).executeAsList()
  }

  fun deleteExamNotesOfPeriod(periodId: Long) {
    queries.deleteAllExamNotesFromPeriod(periodId)
  }
}
