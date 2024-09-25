package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.AcademicPeriodTable
import mehiz.abdallah.progres.data.db.ProgresDB

class AcademicPeriodDao(
  db: ProgresDB
) {

  private val queries = db.academicPeriodTableQueries

  fun insert(table: AcademicPeriodTable) {
    with(table) {
      queries.insert(
        id = id,
        periodStringLatin = periodStringLatin,
        periodStringArabic = periodStringArabic,
        academicYearId = academicYearId,
        academicYearStringLatin = academicYearStringLatin,
        academicYearStringArabic = academicYearStringArabic,
        oofId = oofId
      )
    }
  }

  fun getAllAcademicPeriods(): List<AcademicPeriodTable> {
    return queries.getAllAcademicPeriods().executeAsList()
  }

  fun deleteAllAcademicPeriods() {
    queries.deleteAllAcademicPeriods()
  }
}
