package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.GroupsDao
import mehiz.abdallah.progres.data.db.GroupTable
import mehiz.abdallah.progres.domain.models.GroupModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable

class GroupUseCase(
  private val api: ProgresApi,
  private val groupsDao: GroupsDao,
  private val userAuthUseCase: UserAuthUseCase,
  private val studentCardUseCase: StudentCardUseCase,
) {
  suspend fun getAllGroups(refresh: Boolean): List<GroupModel> {
    groupsDao.getAllGroups().let {
      if (it.isNotEmpty() && !refresh) return it.map { it.toModel() }
    }
    val studentCards = studentCardUseCase.getAllStudentCards(refresh)
    val groups = mutableListOf<GroupTable>()
    val token = userAuthUseCase.getToken()
    studentCards.forEach { card ->
      groups.addAll(
        api.getGroups(card.id, token).filter { it.nomSection != null }.map { it.toTable() },
      )
    }
    if (refresh) groupsDao.deleteAllGroups()
    groups.forEach(groupsDao::insert)
    return groups.map { it.toModel() }
  }
}
