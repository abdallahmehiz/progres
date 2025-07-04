package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.IndividualInfoTable
import mehiz.abdallah.progres.data.db.ProgresDB
import utils.FileStorageManager

class IndividualInfoDao(
  db: ProgresDB,
  private val fileStorageManager: FileStorageManager
) {

  private val queries = db.individualInfoTableQueries

  fun insert(individualInfo: IndividualInfoTable) {
    with(individualInfo) {
      queries.insert(
        id = id,
        uuid = uuid,
        firstNameArabic = firstNameArabic,
        firstNameLatin = firstNameLatin,
        lastNameLatin = lastNameLatin,
        lastNameArabic = lastNameArabic,
        photoPath = photoPath,
        dateOfBirth = dateOfBirth,
        placeOfBirthLatin = placeOfBirthLatin,
        placeOfBirthArabic = placeOfBirthArabic,
      )
    }
  }

  fun getIndividualInfoById(id: Long): IndividualInfoTable? {
    return queries.getById(id).executeAsOneOrNull()
  }

  fun getIndividualPhotoPathById(id: Long): String? {
    return queries.getStudentPhotoPathById(id).executeAsOneOrNull()
  }

  suspend fun getIndividualPhotoById(id: Long): ByteArray? {
    val photoPath = getIndividualPhotoPathById(id)
    return photoPath?.let { fileStorageManager.loadImage(it) }
  }

  suspend fun deleteAllIndividualInfo() {
    // Delete all profile pictures before clearing database
    fileStorageManager.deleteAllProfilePictures()
    queries.delete()
  }

  fun getIndividualInfo(): IndividualInfoTable? {
    return queries.get().executeAsOneOrNull()
  }
}
