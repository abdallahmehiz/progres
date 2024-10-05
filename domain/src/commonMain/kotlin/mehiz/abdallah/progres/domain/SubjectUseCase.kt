package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.SubjectsDao
import mehiz.abdallah.progres.data.db.SubjectTable
import mehiz.abdallah.progres.domain.models.SubjectModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable

class SubjectUseCase(
  private val api: ProgresApi,
  private val subjectsDao: SubjectsDao,
  private val studentCardUseCase: StudentCardUseCase,
  private val userAuthUseCase: UserAuthUseCase,
) {

  suspend fun getAllSubjects(refresh: Boolean, propagateRefresh: Boolean): List<SubjectModel> {
    subjectsDao.getAllSubjects().let {
      if (it.isNotEmpty() && !refresh) return it.map { it.toModel() }
    }
    val studentCards = studentCardUseCase.getAllStudentCards(propagateRefresh)
    val token = userAuthUseCase.getToken()
    val subjects = mutableListOf<SubjectTable>()
    studentCards.forEach { card ->
      subjects.addAll(api.getSubjects(card.openingTrainingOfferId, card.levelId, token).map { it.toTable() })
    }
    if (refresh) subjectsDao.deleteAllSubjects()
    return subjects.map {
      subjectsDao.insert(it)
      it.toModel()
    }
  }
}
