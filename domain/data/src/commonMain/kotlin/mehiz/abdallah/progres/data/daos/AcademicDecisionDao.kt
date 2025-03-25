package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.AcademicDecisionTable
import mehiz.abdallah.progres.data.db.ProgresDB

class AcademicDecisionDao(
  db: ProgresDB
) {

  private val queries = db.academicDecisionTableQueries

  fun insert(table: AcademicDecisionTable) {
    with(table) {
      queries.insert(
        yearPeriodCode = yearPeriodCode,
        decisionStringLatin = decisionStringLatin,
        decisionStringArabic = decisionStringArabic,
        average = average,
        creditAcquired = creditAcquired,
      )
    }
  }

  fun getAllAcademicDecisions(): List<AcademicDecisionTable> {
    return queries.getAllAcademicDecisions().executeAsList()
  }

  fun deleteAllAcademicDecisions() {
    queries.deleteAllAcademicDecisions()
  }
}
