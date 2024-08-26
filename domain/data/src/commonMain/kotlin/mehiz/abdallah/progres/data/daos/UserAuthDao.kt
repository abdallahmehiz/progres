package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.UserAuthTable

class UserAuthDao(
  db: ProgresDB
) {
  private val queries = db.userAuthTableQueries

  suspend fun insert(userAuth: UserAuthTable) {
    with(userAuth) {
      queries.insert(
        individualId = individualId,
        token = token,
        expirationDate = expirationDate,
        userId = userId,
        uuid = uuid,
        establishmentId = establishmentId,
        userName = userName
      )
    }
  }

  suspend fun getToken(): String {
    return queries.getToken().executeAsOne()
  }

  suspend fun getUsername(): String {
    return queries.getUsername().executeAsOne()
  }

  suspend fun getExpirationDate(): String {
    return queries.getTokenExpirationDate().executeAsOne()
  }

  suspend fun getUserAuth(): UserAuthTable {
    return queries.getFullUserAuth().executeAsOne()
  }

  suspend fun getUuid(): String {
    return queries.getStudentUuid().executeAsOne()
  }
}
