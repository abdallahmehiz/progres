package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.StudentCardTable
import utils.FileStorageManager

@Suppress("TooManyFunctions")
class StudentCardDao(
  db: ProgresDB,
  private val fileStorageManager: FileStorageManager
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
        establishmentLogoPath = establishmentLogoPath,
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

  suspend fun deleteCard(id: Long) {
    // Get the establishment logo path before deleting
    val card = queries.getCard(id).executeAsOneOrNull()
    card?.establishmentLogoPath?.let { logoPath ->
      fileStorageManager.deleteImage(logoPath)
    }
    queries.deleteCardWithId(id)
  }

  suspend fun deleteAllCards() {
    // Delete all university logos before clearing database
    fileStorageManager.deleteAllUniversityLogos()
    queries.deleteAllCards()
  }
}
