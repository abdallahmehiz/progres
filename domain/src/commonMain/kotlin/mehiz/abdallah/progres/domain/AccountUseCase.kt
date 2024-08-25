package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.UserAuthDao
import mehiz.abdallah.progres.domain.models.toUserAuthTable

class AccountUseCase(
  private val api: ProgresApi,
  private val userAuthDao: UserAuthDao,
) {
  suspend fun login(id: String, password: String) {
    try {
      val userAuth = api.login(id, password)
      userAuthDao.insert(userAuth.toUserAuthTable())
    } catch (e: Exception) {
      if (e.message == "connection reset") {
        login(id, password)
      } else {
        throw e
      }
    }
  }
}
