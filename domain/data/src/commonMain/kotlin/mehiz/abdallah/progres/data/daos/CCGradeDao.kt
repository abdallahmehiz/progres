package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.CCGradeTable
import mehiz.abdallah.progres.data.db.ProgresDB

class CCGradeDao(
  db: ProgresDB
) {

  val queries = db.cCGradeTableQueries

  fun insert(table: CCGradeTable) {
    with(table) {
      queries.insert(
        id = id,
        grade = grade,
        wasAbsent = wasAbsent,
        observation = observation,
        yearPeriodCode = yearPeriodCode,
        ap = ap,
        subjectStringLatin = subjectStringLatin,
        subjectStringArabic = subjectStringArabic,
        appealStartDate = appealStartDate,
        appealEndDate = appealEndDate,
        appealable = appealable
      )
    }
  }

  fun getAllCCGrades(): List<CCGradeTable> {
    return queries.getAllCCGrades().executeAsList()
  }

  fun getCCGradesByPeriodId(yearPeriodCode: String): List<CCGradeTable> {
    return queries.getGradesFromByYearPeriodCode(yearPeriodCode).executeAsList()
  }

  fun deleteAllCCGrades() {
    queries.deleteAllCCGrades()
  }
}
