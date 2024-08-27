@file:OptIn(ExperimentalUuidApi::class)

package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.UserAuthDto
import mehiz.abdallah.progres.data.db.UserAuthTable
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

fun UserAuthDto.toUserAuthTable(): UserAuthTable {
  return UserAuthTable(
    individualId = individualId,
    token = token,
    expirationDate = expirationDate,
    userId = userId,
    uuid = uuid.toString(),
    establishmentId = establishmentId,
    userName = userName
  )
}