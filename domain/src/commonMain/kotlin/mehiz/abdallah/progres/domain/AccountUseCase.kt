package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.ExamGradesDao
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.data.daos.UserAuthDao
import mehiz.abdallah.progres.data.db.ExamGradeTable
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import mehiz.abdallah.progres.domain.models.toUserAuthTable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AccountUseCase(
  private val api: ProgresApi,
  private val userAuthDao: UserAuthDao,
  private val examGradesDao: ExamGradesDao,
  private val studentCardDao: StudentCardDao,
  private val individualInfoDao: IndividualInfoDao,
) {
  suspend fun login(id: String, password: String) {
    try {
      val userAuth = api.login(id, password)
      userAuthDao.insert(userAuth.toUserAuthTable())
    } catch (e: Exception) {
      if (e.message == "Connection reset") {
        login(id, password)
      } else {
        throw e
      }
    }
  }

  suspend fun getStudentPhoto(): ByteArray {
    return getLatestStudentCard().photo
  }

  suspend fun getStudentCards(): List<StudentCardModel> {
    return try {
      val cards = studentCardDao.getAllStudentCards()
      if (cards.isNotEmpty()) return cards.map { it.toModel() }
      val uuid = userAuthDao.getUuid()
      val token = userAuthDao.getToken()
      api.getStudentCards(uuid, token)
        .map {
          it.toTable(
            api.getStudentPhoto(uuid, token),
            api.getEstablishmentLogo(it.refEtablissementId, token),
            api.getTransportState(uuid, it.id, token)?.transportPayed ?: false,
          )
        }
        .map { it.also { studentCardDao.insert(it) } }
        .map { it.toModel() }
    } catch (e: Exception) {
      if (e.message == "Connection reset") getStudentCards() else throw e
    }
  }

  suspend fun getLatestStudentCard(): StudentCardModel {
    return getStudentCards().maxBy { it.id }
  }

  suspend fun getIndividualInfo(): IndividualInfoModel {
    individualInfoDao.getIndividualInfo()?.let { return it.toModel() }
    val info = api.getIndividualInfo(userAuthDao.getUuid(), userAuthDao.getToken())
    return info.let { dto ->
      dto.toTable(getStudentPhoto()).also { individualInfoDao.insert(it) }
    }.toModel()
  }

  // returns ALL of the student's exams available from their student cards
  suspend fun getExamGrades(): List<ExamGradeModel> {
    examGradesDao.getAllExamGrades().let {
      if (it.isNotEmpty()) return it.map { note -> note.toModel() }
    }
    val studentCards = getStudentCards()
    var examNotes = mutableListOf<ExamGradeTable>()
    studentCards.forEach { card ->
      examNotes.addAll(api.getExamGrades(card.id, userAuthDao.getToken()).map { it.toTable() })
    }
    examNotes.map { examGradesDao.insert(it) }
    return examGradesDao.getAllExamGrades().map { it.toModel() }
  }
}
