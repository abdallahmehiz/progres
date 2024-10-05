package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.ExamGradeDao
import mehiz.abdallah.progres.data.db.ExamGradeTable
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable

class ExamGradeUseCase(
  private val api: ProgresApi,
  private val examGradeDao: ExamGradeDao,
  private val academicPeriodUseCase: AcademicPeriodUseCase,
  private val studentCardUseCase: StudentCardUseCase,
  private val userAuthUseCase: UserAuthUseCase
) {

  // returns ALL of the student's exams available from their student cards
  suspend fun getExamGrades(refresh: Boolean, propagateRefresh: Boolean): List<ExamGradeModel> {
    val academicPeriods = academicPeriodUseCase.getAcademicPeriods(refresh, propagateRefresh)
    examGradeDao.getAllExamGrades().let { grades ->
      if (grades.isNotEmpty() && !refresh) {
        return grades.map { grade ->
          grade.toModel(academicPeriods.first { it.yearPeriodCode == grade.yearPeriodCode })
        }
      }
    }
    val studentCards = studentCardUseCase.getAllStudentCards(refresh)
    val token = userAuthUseCase.getToken()
    val examGrades = mutableListOf<ExamGradeTable>()
    studentCards.forEach { card ->
      examGrades.addAll(
        api.getExamGrades(card.id, token).map { grade ->
          grade.toTable(
            academicPeriods.first { card.openingTrainingOfferId == it.oofId && grade.idPeriode == it.id }
              .yearPeriodCode,
          )
        },
      )
    }
    if (refresh) examGradeDao.deleteAllExamGrades()
    examGrades.forEach { examGradeDao.insert(it) }
    return examGrades.map { grade ->
      grade.toModel(
        academicPeriods.first { it.yearPeriodCode == grade.yearPeriodCode },
      )
    }
  }
}
