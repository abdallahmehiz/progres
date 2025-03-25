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
  private val academicPeriodUseCase: AcademicPeriodUseCase,
  private val studentCardUseCase: StudentCardUseCase,
  private val userAuthUseCase: UserAuthUseCase,
) {

  suspend fun getAllSubjects(refresh: Boolean, propagateRefresh: Boolean): List<SubjectModel> {
    val academicPeriods = academicPeriodUseCase.getAcademicPeriods(propagateRefresh, propagateRefresh)
    subjectsDao.getAllSubjects().let {
      if (it.isNotEmpty() && !refresh) {
        return it.mapNotNull { subject ->
          academicPeriods.firstOrNull { it.yearPeriodCode == subject.yearPeriodCode }?.let { period ->
            subject.toModel(period)
          }
        }
      }
    }
    val studentCards = studentCardUseCase.getAllStudentCards(propagateRefresh)
    val token = userAuthUseCase.getToken()
    val subjects = mutableListOf<SubjectTable>()
    studentCards.forEach { card ->
      subjects.addAll(
        api.getSubjects(card.openingTrainingOfferId, card.levelId, token).mapNotNull { subject ->
          val period = academicPeriods.firstOrNull {
            it.oofId == card.openingTrainingOfferId && it.periodStringLatin == subject.periodeLibelleFr
          }
          period?.let { subject.toTable(it.yearPeriodCode) }
        },
      )
    }
    if (refresh) subjectsDao.deleteAllSubjects()
    return subjects.mapNotNull {
      subjectsDao.insert(it)
      academicPeriods.firstOrNull { period ->
        period.yearPeriodCode == it.yearPeriodCode
      }?.let { period -> it.toModel(period) }
    }
  }
}
