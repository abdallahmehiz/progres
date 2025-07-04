package mehiz.abdallah.progres.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import utils.FileStorageManager

/**
 * Handles migration of image data from database BLOBs to file storage
 * This is used during the transition period to ensure compatibility
 */
class ImageMigrationUseCase(
    private val individualInfoDao: IndividualInfoDao,
    private val studentCardDao: StudentCardDao,
    private val fileStorageManager: FileStorageManager
) {

    /**
     * Checks if migration is needed and suggests a refresh
     * Since this is a significant change, we recommend users refresh their data
     * to get images stored in the new file format
     */
    suspend fun isMigrationNeeded(): Boolean = withContext(Dispatchers.IO) {
        // Check if there are any records that might have old BLOB data
        // Since we changed schema, old data will be cleaned during refresh
        val individualInfo = individualInfoDao.getIndividualInfo()
        val studentCards = studentCardDao.getAllStudentCards()

        // If no data exists, no migration needed
        if (individualInfo == null && studentCards.isEmpty()) {
            return@withContext false
        }

        // Check if we have photo paths (new format) or if they're missing (needs refresh)
        val needsPhotoMigration = individualInfo?.photoPath == null
        val needsLogoMigration = studentCards.any { it.establishmentLogoPath == null }

        needsPhotoMigration || needsLogoMigration
    }

    /**
     * Clears all data to force a fresh sync with the new file storage format
     * This is the safest migration approach for this type of schema change
     */
    suspend fun forceFreshSync() = withContext(Dispatchers.IO) {
        // Clean up any existing files
        fileStorageManager.deleteAllProfilePictures()
        fileStorageManager.deleteAllUniversityLogos()

        // Clear database to force fresh sync
        individualInfoDao.deleteAllIndividualInfo()
        studentCardDao.deleteAllCards()
    }
}
