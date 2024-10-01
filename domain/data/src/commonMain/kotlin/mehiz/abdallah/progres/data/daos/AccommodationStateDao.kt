package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.AccommodationStateTable
import mehiz.abdallah.progres.data.db.ProgresDB

class AccommodationStateDao(
  db: ProgresDB
) {
  private val queries = db.accommodationStateTableQueries

  fun insert(table: AccommodationStateTable) {
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

  fun getAllAccommodationsState(): List<AccommodationStateTable> {
    return queries.getAllAccommodationStates().executeAsList()
  }

  fun getAccommodationStateByCardId(cardId: Long): AccommodationStateTable? {
    return queries.getAccommodationStateForCardId(cardId).executeAsOneOrNull()
  }

  fun deleteAllAccommodationStates() {
    queries.deleteAllAccommodations()
  }
}
