package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.StudentCardTable

@Suppress("TooManyFunctions")
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
        ofSpecialtyId = ofSpecialtyId,
        ofSpecialtyCode = ofSpecialtyCode,
        ofSpecialtyStringLatin = ofSpecialtyStringLatin,
        ofSpecialtyStringArabic = ofSpecialtyStringArabic,
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
        nationalIdNumber = nationalIdNumber,
        situationId = situationId,
        openingTrainingOfferId = openingTrainingOfferId,
        isTransportPaid = isTransportPaid,
        isRegistrationFeePaid = isRegistrationFeePaid,
      )
    }
  }

  fun getAllStudentCards(): List<StudentCardTable> {
    return queries.getAllStudentCards().executeAsList()
  }

  fun getStudentCard(id: Long): StudentCardTable {
    return queries.getCard(id).executeAsOne()
  }

  fun getLatestStudentCard(): StudentCardTable? {
    return queries.getLatestStudentCard().executeAsOneOrNull()
  }

  fun getCardByAcademicYear(id: Long): StudentCardTable {
    return queries.getCardByAcademicYear(id).executeAsOne()
  }

  fun deleteCard(id: Long) {
    queries.deleteCardWithId(id)
  }

  fun deleteAllCards() {
    queries.deleteAllCards()
  }
}
