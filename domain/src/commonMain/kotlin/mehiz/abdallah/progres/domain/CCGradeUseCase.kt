package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.CCGradeDao
import mehiz.abdallah.progres.data.db.CCGradeTable
import mehiz.abdallah.progres.domain.models.CCGradeModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable

class CCGradeUseCase(
  private val api: ProgresApi,
  private val ccGradeDao: CCGradeDao,
  private val academicPeriodUseCase: AcademicPeriodUseCase,
  private val studentCardUseCase: StudentCardUseCase,
  private val userAuthUseCase: UserAuthUseCase,
) {
  suspend fun getAllCCGrades(refresh: Boolean): List<CCGradeModel> {
    val academicPeriods = academicPeriodUseCase.getAcademicPeriods(refresh, true)
    ccGradeDao.getAllCCGrades().let { grades ->
      if (grades.isNotEmpty() && !refresh) {
        return grades.map { grade ->
          grade.toModel(academicPeriods.first { it.yearPeriodCode == grade.yearPeriodCode })
        }
      }
    }
    val cards = studentCardUseCase.getAllStudentCards(refresh)
    val token = userAuthUseCase.getToken()
    val grades = mutableListOf<CCGradeTable>()
    cards.forEach { card ->
      grades.addAll(
        api.getCCGrades(card.id, token).mapNotNull { grade ->
          academicPeriods.firstOrNull {
            grade.llPeriode == it.periodStringLatin && card.openingTrainingOfferId == it.oofId
          }?.yearPeriodCode?.let { grade.toTable(it) }
        },
      )
    }
    if (refresh) ccGradeDao.deleteAllCCGrades()
    return grades.map { grade ->
      ccGradeDao.insert(grade)
      grade.toModel(academicPeriods.first { it.yearPeriodCode == grade.yearPeriodCode })
    }
  }
}
