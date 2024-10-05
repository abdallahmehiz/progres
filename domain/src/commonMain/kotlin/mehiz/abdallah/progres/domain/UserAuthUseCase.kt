package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.UserAuthDao
import mehiz.abdallah.progres.domain.models.UserAuthModel
import mehiz.abdallah.progres.domain.models.toUserAuthModel
import mehiz.abdallah.progres.domain.models.toUserAuthTable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserAuthUseCase(
  private val api: ProgresApi,
  private val userAuthDao: UserAuthDao
) {
  suspend fun login(id: String, password: String) {
    try {
      val userAuth = api.login(id, password)
      userAuthDao.insert(userAuth.toUserAuthTable())
    } catch (e: Exception) {
      if (e.message == "Connection reset") {
        login(id, password)
      } else {
        throw e
      }
    }
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
}
