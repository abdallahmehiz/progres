package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.ExamScheduleDao
import mehiz.abdallah.progres.data.db.ExamScheduleTable
import mehiz.abdallah.progres.domain.models.ExamScheduleModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable

class ExamScheduleUseCase(
  private val api: ProgresApi,
  private val examScheduleDao: ExamScheduleDao,
  private val studentCardUseCase: StudentCardUseCase,
  private val academicPeriodUseCase: AcademicPeriodUseCase,
  private val userAuthUseCase: UserAuthUseCase,
) {
  suspend fun getExamSchedules(refresh: Boolean, propagateRefresh: Boolean): List<ExamScheduleModel> {
    val academicPeriods = academicPeriodUseCase.getAcademicPeriods(refresh, propagateRefresh)
    examScheduleDao.getAllExamSchedules().let {
      if (it.isNotEmpty() && !refresh) {
        return it.map { examSchedule ->
          examSchedule.toModel(
            academicPeriods.first { examSchedule.yearPeriodCode == it.yearPeriodCode },
          )
        }
      }
    }
    val studentCards = studentCardUseCase.getAllStudentCards(refresh)
    val examSchedules = mutableListOf<ExamScheduleTable>()
    val token = userAuthUseCase.getToken()
    studentCards.forEach { card ->
      examSchedules.addAll(
        api.getExamsScheduleForPeriod(card.openingTrainingOfferId, card.levelId, token).map { examSchedule ->
          examSchedule.toTable(
            academicPeriods.first {
              it.oofId == card.openingTrainingOfferId && it.periodStringLatin == examSchedule.libellePeriode
            }.yearPeriodCode,
          )
        },
      )
    }
    if (refresh) examScheduleDao.deleteAllExamSchedules()
    examSchedules.forEach { examScheduleDao.insert(it) }
    return examSchedules.map { examSchedule ->
      examSchedule.toModel(
        academicPeriods.first {
          it.yearPeriodCode == examSchedule.yearPeriodCode
        },
      )
    }
  }
}
