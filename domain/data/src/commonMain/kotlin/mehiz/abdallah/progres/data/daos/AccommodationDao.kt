package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.AccommodationTable
import mehiz.abdallah.progres.data.db.ProgresDB

class AccommodationDao(
  db: ProgresDB
) {
  private val queries = db.accommodationTableQueries

  fun insert(table: AccommodationTable) {
    with(table) {
      queries.insert(
        id = id,
        academicYearId = academicYearId,
        providerId = providerId,
        providerStringLatin = providerStringLatin,
        providerStringArabic = providerStringArabic,
        residenceStringLatin = residenceStringLatin,
        residenceStringArabic = residenceStringArabic,
        assignedPavillion = assignedPavillion
      )
    }
  }

  fun getAllAccommodationsState(): List<AccommodationTable> {
    return queries.getAllAccommodations().executeAsList()
  }

  fun deleteAllAccommodationStates() {
    queries.deleteAllAccommodations()
  }
}
