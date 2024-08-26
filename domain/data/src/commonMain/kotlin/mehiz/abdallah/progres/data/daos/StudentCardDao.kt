package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.StudentCardTable

class StudentCardDao(
  db: ProgresDB
) {
  private val queries = db.studentCardTableQueries

  fun insert(card: StudentCardTable) {
    with(card) {
      queries.insert(
        academicYearString = academicYearString,
        academicYearId = academicYearId,
        cycleId = cycleId,
        cycleCode = cycleCode,
        cycleStringLatin = cycleStringLatin,
        cycleStringArabic = cycleStringArabic,
        ofDomainCode = ofDomainCode,
        ofDomainId = ofDomainId,
        ofDomainStringLatin = ofDomainStringLatin,
        ofDomainStringArabic = ofDomainStringArabic,
        ofFieldId = ofFieldId,
        ofFieldCode = ofFieldCode,
        ofFieldStringArabic = ofFieldStringArabic,
        ofFieldStringLatin = ofFieldStringLatin,
        individualFirstNameLatin = individualFirstNameLatin,
        individualFirstNameArabic = individualFirstNameArabic,
        individualLastNameLatin = individualLastNameLatin,
        individualLastNameArabic = individualLastNameArabic,
        individualDateOfBirth = individualDateOfBirth,
        individualPlaceOfBirthLatin = individualPlaceOfBirthLatin,
        individualPlaceOfBirthArabic = individualPlaceOfBirthArabic,
        id = id,
        registrationNumber = registrationNumber,
        serialNumber = serialNumber,
        levelId = levelId,
        levelRank = levelRank,
        levelString = levelString,
        levelStringLongArabic = levelStringLongArabic,
        levelStringLongLatin = levelStringLongLatin,
        bacMark = bacMark,
        lastMark = lastMark,
        establishmentId = establishmentId,
        establishmentCode = establishmentCode,
        establishmentLogo = establishmentLogo,
        establishmentStringArabic = establishmentStringArabic,
        establishmentStringLatin = establishmentStringLatin,
        nationalIdNumber = nationalIdNumber,
        situationId = situationId,
        openingTraingingOfferId = openingTraingingOfferId,
        isTransportPaid = isTransportPaid,
        photo = photo
      )
    }
  }

  fun getAllStudentCards(): List<StudentCardTable> {
    return queries.getAllStudentCards().executeAsList()
  }

  fun getStudentCard(id: Long): StudentCardTable {
    return queries.getCard(id).executeAsOne()
  }

  fun getLatestStudentCard(): StudentCardTable {
    return queries.getLatestStudentCard().executeAsOne()
  }

  fun getStudentPhoto(id: Long): ByteArray {
    return queries.getStudentPhoto(id).executeAsOne()
  }

  fun getLatestStudentPhoto(): ByteArray {
    return queries.getLatestStudentPhoto().executeAsOne()
  }

  fun getEstablishmentLogo(id: Long): ByteArray {
    return queries.getEstablishmentLogo(id).executeAsOne()
  }

  fun getLatestEstablishmentLogo(): ByteArray {
    return queries.getLatestEstablishmentLogo().executeAsOne()
  }

  fun deleteCard(id: Long) {
    queries.deleteCardWithId(id)
  }

  fun deleteAllCards() {
    queries.deleteAllCards()
  }
}
