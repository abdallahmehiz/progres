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
}
