@file:OptIn(ExperimentalUuidApi::class)

package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.UserAuthDto
import mehiz.abdallah.progres.data.db.UserAuthTable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class UserAuthModel(
  val userId: Long,
  val individualId: Long,
  val establishmentId: Long,
  val userName: String,
  val uuid: Uuid,
  val tokenExpirationDate: String
)

@OptIn(ExperimentalUuidApi::class)
fun UserAuthTable.toUserAuthModel(): UserAuthModel {
  return UserAuthModel(
    userId = userId,
    individualId = individualId,
    establishmentId = establishmentId,
    userName = userName,
    uuid = Uuid.parse(uuid),
    tokenExpirationDate = expirationDate
  )
}

@OptIn(ExperimentalEncodingApi::class)
fun UserAuthDto.toUserAuthTable(password: String): UserAuthTable {
  return UserAuthTable(
    individualId = individualId,
    token = token,
    expirationDate = expirationDate,
    userId = userId,
    uuid = uuid,
    establishmentId = establishmentId,
    userName = userName,
    password64 = Base64.encode(password.encodeToByteArray())
  )
}
