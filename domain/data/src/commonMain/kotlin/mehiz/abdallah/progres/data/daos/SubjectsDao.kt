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
        oofId = oofId,
        ofStringLatin = ofStringLatin,
        periodId = periodId,
        periodStringLatin = periodStringLatin,
        periodStringArabic = periodStringArabic,
        levelStringLatin = levelStringLatin,
        levelStringArabic = levelStringArabic,
        subjectId = subjectId,
        subjectStringLatin = subjectStringLatin,
        subjectStringArabic = subjectStringArabic,
        subjectCoefficient = subjectCoefficient,
        subjectCredit = subjectCredit,
        subjectCCCoefficient = subjectCCCoefficient,
        subjectCICoefficient = subjectCICoefficient,
        subjectExamCoefficient = subjectExamCoefficient
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
