package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.UserAuthTable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
        userName = userName,
        password64 = password64
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

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getUuid(): Uuid {
    return Uuid.parse(queries.getStudentUuid().executeAsOne())
  }

  suspend fun getEstablishmentId(): Long {
    return queries.getEstablishmentId().executeAsOne()
  }

  suspend fun getBase64EncodedPassword(): String? {
    return queries.getBase64EncodedPassword().executeAsOneOrNull()?.password64
  }

  suspend fun deleteUserAuth() {
    queries.deleteUserAuth()
  }
}
