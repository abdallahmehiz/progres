package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.SubjectTable

class SubjectsDao(
  db: ProgresDB
) {
  private val queries = db.subjectTableQueries

  fun insert(subject: SubjectTable) {
    with(subject) {
      queries.insert(
        id = id,
        subjectStringLatin = subjectStringLatin,
        subjectStringArabic = subjectStringArabic,
        subjectCCCoefficient = subjectCCCoefficient,
        subjectCICoefficient = subjectCICoefficient,
        subjectExamCoefficient = subjectExamCoefficient,
        yearPeriodCode = yearPeriodCode
      )
    }
  }

  fun getAllSubjects(): List<SubjectTable> {
    return queries.getAllSubjects().executeAsList()
  }

  fun deleteAllSubjects() {
    queries.deleteAllSubjects()
  }
}
