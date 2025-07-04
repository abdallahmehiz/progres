package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Manages file storage operations for profile pictures and university logos
 */
class FileStorageManager(
  private val baseDataPath: String
) {
  private val fileSystem = FileSystem.SYSTEM
  private val imagesDir = "$baseDataPath/images".toPath()
  private val profilePicturesDir = imagesDir / "profiles"
  private val universityLogosDir = imagesDir / "logos"

  init {
    // Ensure directories exist
    try {
      fileSystem.createDirectories(profilePicturesDir)
      fileSystem.createDirectories(universityLogosDir)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * Saves a profile picture and returns the file path
   */
  suspend fun saveProfilePicture(studentId: String, imageData: ByteArray): String? = withContext(Dispatchers.IO) {
    try {
      val fileName = "profile_${studentId.replace(Regex("[^a-zA-Z0-9]"), "_")}.jpg"
      val filePath = profilePicturesDir / fileName
      
      fileSystem.write(filePath) {
        write(imageData)
      }
      
      filePath.toString()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Saves a university logo and returns the file path
   */
  suspend fun saveUniversityLogo(establishmentId: String, imageData: ByteArray): String? = withContext(Dispatchers.IO) {
    try {
      val fileName = "logo_${establishmentId.replace(Regex("[^a-zA-Z0-9]"), "_")}.jpg"
      val filePath = universityLogosDir / fileName
      
      fileSystem.write(filePath) {
        write(imageData)
      }
      
      filePath.toString()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Loads image data from a file path
   */
  suspend fun loadImage(filePath: String): ByteArray? = withContext(Dispatchers.IO) {
    try {
      val path = filePath.toPath()
      if (fileSystem.exists(path)) {
        fileSystem.read(path) {
          readByteArray()
        }
      } else {
        null
      }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Deletes an image file
   */
  suspend fun deleteImage(filePath: String): Boolean = withContext(Dispatchers.IO) {
    try {
      val path = filePath.toPath()
      if (fileSystem.exists(path)) {
        fileSystem.delete(path)
        true
      } else {
        false
      }
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  /**
   * Deletes all profile pictures
   */
  suspend fun deleteAllProfilePictures(): Boolean = withContext(Dispatchers.IO) {
    try {
      if (fileSystem.exists(profilePicturesDir)) {
        fileSystem.deleteRecursively(profilePicturesDir)
        fileSystem.createDirectories(profilePicturesDir)
        true
      } else {
        true
      }
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  /**
   * Deletes all university logos
   */
  suspend fun deleteAllUniversityLogos(): Boolean = withContext(Dispatchers.IO) {
    try {
      if (fileSystem.exists(universityLogosDir)) {
        fileSystem.deleteRecursively(universityLogosDir)
        fileSystem.createDirectories(universityLogosDir)
        true
      } else {
        true
      }
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  /**
   * Checks if an image file exists
   */
  fun imageExists(filePath: String): Boolean {
    return try {
      fileSystem.exists(filePath.toPath())
    } catch (e: Exception) {
      false
    }
  }
}