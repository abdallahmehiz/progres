package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.UserAuth

class UserAuthDao(
  db: ProgresDB
) {
  private val queries = db.userAuthQueries

  suspend fun insert(userAuth: UserAuth) {
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
