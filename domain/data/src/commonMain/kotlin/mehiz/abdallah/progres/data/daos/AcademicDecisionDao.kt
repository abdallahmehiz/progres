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
        id = id,
        periodId = periodId,
        type = type,
        decisionStringLatin = decisionStringLatin,
        decisionStringArabic = decisionStringArabic,
        average = average,
        avearageSn = avearageSn,
        credit = credit,
        creditObtained = creditObtained,
        creditAcquired = creditAcquired,
        cumulatedCredit = cumulatedCredit,
        isAnnual = isAnnual
      )
    }
  }

  fun getAllAcademicDecisions(): List<AcademicDecisionTable> {
    return queries.getAllAcademicDecisions().executeAsList()
  }

  fun deleteAllAcademicDecisions() {
    queries.deleteAllAcademicDecisions()
  }

  fun getAcademicDecisionById(id: Long): AcademicDecisionTable? {
    return queries.getAcademicDecisionById(id).executeAsOneOrNull()
  }
}
