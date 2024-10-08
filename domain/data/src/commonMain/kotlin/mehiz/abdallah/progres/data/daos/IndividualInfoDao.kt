package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.IndividualInfoTable
import mehiz.abdallah.progres.data.db.ProgresDB

class IndividualInfoDao(
  db: ProgresDB
) {

  private val queries = db.individualInfoTableQueries

  fun insert(individualInfo: IndividualInfoTable) {
    with(individualInfo) {
      queries.insert(
        id = id,
        uuid = uuid,
        identifier = identifier,
        firstNameArabic = firstNameArabic,
        firstNameLatin = firstNameLatin,
        lastNameLatin = lastNameLatin,
        lastNameArabic = lastNameArabic,
        photo = photo,
        dateOfBirth = dateOfBirth,
        placeOfBirthLatin = placeOfBirthLatin,
        placeOfBirthArabic = placeOfBirthArabic,
        cardId = cardId
      )
    }
  }

  fun getIndividualInfoById(id: Long): IndividualInfoTable? {
    return queries.getById(id).executeAsOneOrNull()
  }

  fun getIndividualPhotoById(id: Long): ByteArray? {
    return queries.getStudentPhotoById(id).executeAsOne().photo
  }

  fun deleteAllIndividualInfo() {
    queries.delete()
  }

  fun getIndividualInfo(): IndividualInfoTable? {
    return queries.get().executeAsOneOrNull()
  }
}
