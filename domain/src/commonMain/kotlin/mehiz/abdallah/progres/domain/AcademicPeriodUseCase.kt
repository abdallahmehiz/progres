package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.AcademicPeriodDao
import mehiz.abdallah.progres.data.db.AcademicPeriodTable
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable

class AcademicPeriodUseCase(
  private val api: ProgresApi,
  private val academicPeriodDao: AcademicPeriodDao,
  private val userAuthUseCase: UserAuthUseCase,
  private val studentCardUseCase: StudentCardUseCase,
) {

  suspend fun getCurrentAcademicPeriod(refresh: Boolean, propagateRefresh: Boolean): AcademicPeriodModel {
    val currentStudentCard = studentCardUseCase.getLatestStudentCard(false)
    return getAcademicPeriods(refresh, propagateRefresh).filterNot {
      currentStudentCard.academicYearId == it.academicYearId
    }.maxBy { it.id }
  }

  suspend fun getAcademicPeriods(refresh: Boolean, propagateRefresh: Boolean): List<AcademicPeriodModel> {
    academicPeriodDao.getAllAcademicPeriods().let {
      if (it.isNotEmpty() && !refresh) return it.map { it.toModel() }
    }
    val studentCards = studentCardUseCase.getAllStudentCards(propagateRefresh)
    val token = userAuthUseCase.getToken()
    val periods = mutableListOf<AcademicPeriodTable>()
    studentCards.forEach { card ->
      periods.addAll(
        api.getAcademicPeriods(card.levelId, token).map {
          it.toTable(
            oofId = card.openingTrainingOfferId,
            academicYearId = card.academicYearId,
            academicYearCode = card.academicYearString,
            academicYearStringLatin = card.levelStringLongLatin,
            academicYearStringArabic = card.levelStringLongArabic,
          )
        },
      )
    }
    if (refresh) academicPeriodDao.deleteAllAcademicPeriods()
    return periods.map {
      academicPeriodDao.insert(it)
      it.toModel()
    }
  }
}
