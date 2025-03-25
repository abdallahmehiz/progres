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
        ofDomainStringLatin = ofDomainStringLatin,
        ofDomainStringArabic = ofDomainStringArabic,
        ofFieldStringArabic = ofFieldStringArabic,
        ofFieldStringLatin = ofFieldStringLatin,
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
        levelStringLongArabic = levelStringLongArabic,
        levelStringLongLatin = levelStringLongLatin,
        situationId = situationId,
        openingTrainingOfferId = openingTrainingOfferId,
        isTransportPaid = isTransportPaid,
        levelId = levelId,
        establishmentStringLatin = establishmentStringLatin,
        establishmentStringArabic = establishmentStringArabic,
        establishmentLogo = establishmentLogo,
        cycleStringLatin = cycleStringLatin,
        cycleStringArabic = cycleStringArabic
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
