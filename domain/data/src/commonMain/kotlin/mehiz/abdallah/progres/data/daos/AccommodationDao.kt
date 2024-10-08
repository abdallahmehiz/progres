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
        cardId = cardId,
        studentFolderId = studentFolderId,
        academicYearId = academicYearId,
        academicYearString = academicYearString,
        providerId = providerId,
        providerStringLatin = providerStringLatin,
        providerStringArabic = providerStringArabic,
        residenceId = residenceId,
        residenceStringLatin = residenceStringLatin,
        residenceStringArabic = residenceStringArabic,
        accommodationRequestDate = accommodationRequestDate,
        renewalRequestDate = renewalRequestDate,
        isARenewalRequest = isARenewalRequest,
        isARenewal = isARenewal,
        isApproved = isApproved,
        approvalDate = approvalDate,
        assignedPavillion = assignedPavillion,
        isAccommodationPaid = isAccommodationPaid
      )
    }
  }

  fun getAllAccommodationsState(): List<AccommodationTable> {
    return queries.getAllAccommodations().executeAsList()
  }

  fun getAccommodationStateByCardId(cardId: Long): AccommodationTable? {
    return queries.getAccommodationForCardId(cardId).executeAsOneOrNull()
  }

  fun deleteAllAccommodationStates() {
    queries.deleteAllAccommodations()
  }
}
