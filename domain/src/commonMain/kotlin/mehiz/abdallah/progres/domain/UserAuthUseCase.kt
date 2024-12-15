package mehiz.abdallah.progres.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.UserAuthDao
import mehiz.abdallah.progres.data.db.UserAuthTable
import mehiz.abdallah.progres.domain.models.UserAuthModel
import mehiz.abdallah.progres.domain.models.toUserAuthModel
import mehiz.abdallah.progres.domain.models.toUserAuthTable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserAuthUseCase(
  private val api: ProgresApi,
  private val userAuthDao: UserAuthDao,
) {
  suspend fun login(id: String, password: String): UserAuthModel {
    return apiLogin(id, password).also { userAuthDao.insert(it) }.toUserAuthModel()
  }

  private suspend fun apiLogin(id: String, password: String): UserAuthTable {
    return try {
      val userAuth = api.login(id, password)
      userAuth.toUserAuthTable(password)
    } catch (e: Exception) {
      if (e.message == "Connection reset") {
        apiLogin(id, password)
      } else {
        throw e
      }
    }
  }

  suspend fun getUsername(): String {
    return userAuthDao.getUsername()
  }

  suspend fun getPassword(): String? {
    return userAuthDao.getBase64EncodedPassword()
  }

  suspend fun getToken(): String {
    return userAuthDao.getToken()
  }

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getUuid(): Uuid {
    return userAuthDao.getUuid()
  }

  suspend fun getFullUserAuth(): UserAuthModel {
    return userAuthDao.getUserAuth().toUserAuthModel()
  }

  suspend fun getExpirationDate(): LocalDateTime {
    return DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(userAuthDao.getExpirationDate()).toLocalDateTime()
  }

  suspend fun refreshLogin(id: String, password: String) {
    val newAuth = apiLogin(id, password)
    userAuthDao.deleteUserAuth()
    userAuthDao.insert(newAuth)
  }
}
