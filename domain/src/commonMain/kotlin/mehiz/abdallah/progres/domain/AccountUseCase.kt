package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.ExamGradesDao
import mehiz.abdallah.progres.data.daos.ExamScheduleDao
import mehiz.abdallah.progres.data.daos.GroupsDao
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.data.daos.UserAuthDao
import mehiz.abdallah.progres.data.db.ExamGradeTable
import mehiz.abdallah.progres.data.db.ExamScheduleTable
import mehiz.abdallah.progres.data.db.GroupTable
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import mehiz.abdallah.progres.domain.models.ExamScheduleModel
import mehiz.abdallah.progres.domain.models.GroupModel
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import mehiz.abdallah.progres.domain.models.toUserAuthTable
import kotlin.uuid.ExperimentalUuidApi

@Suppress("LongParameterList")
@OptIn(ExperimentalUuidApi::class)
class AccountUseCase(
  private val api: ProgresApi,
  private val groupsDao: GroupsDao,
  private val userAuthDao: UserAuthDao,
  private val examGradesDao: ExamGradesDao,
  private val studentCardDao: StudentCardDao,
  private val examScheduleDao: ExamScheduleDao,
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
      api.getStudentCards(uuid, token).map {
        it.toTable(
          api.getStudentPhoto(uuid, token),
          api.getEstablishmentLogo(it.refEtablissementId, token),
          it.transportPaye ?: api.getTransportState(uuid, it.id, token)?.transportPayed ?: false,
        )
      }.map { it.also { studentCardDao.insert(it) } }.map { it.toModel() }
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
      if (it.isNotEmpty()) return it.map { grade -> grade.toModel() }
    }
    val studentCards = getStudentCards()
    var examNotes = mutableListOf<ExamGradeTable>()
    studentCards.forEach { card ->
      examNotes.addAll(api.getExamGrades(card.id, userAuthDao.getToken()).map { it.toTable() })
    }
    examNotes.forEach { examGradesDao.insert(it) }
    return examNotes.map { it.toModel() }
  }

  suspend fun getExamSchedules(): List<ExamScheduleModel> {
    examScheduleDao.getAllExamSchedules().let {
      if (it.isNotEmpty()) return it.map { examSchedule -> examSchedule.toModel() }
    }
    val studentCards = getStudentCards()
    val examSchedules = mutableListOf<ExamScheduleTable>()
    val token = userAuthDao.getToken()
    studentCards.forEach { card ->
      examSchedules.addAll(
        api.getExamsScheduleForPeriod(card.openingTrainingOfferId, card.levelId, token).map {
          it.toTable()
        },
      )
    }
    examSchedules.forEach { examScheduleDao.insert(it) }
    return examSchedules.map { it.toModel() }
  }

  suspend fun getAllGroups(): List<GroupModel> {
    groupsDao.getAllGroups().let {
      if (it.isNotEmpty()) return it.map { it.toModel() }
    }
    val studentCards = getStudentCards()
    val groups = mutableListOf<GroupTable>()
    val token = userAuthDao.getToken()
    studentCards.forEach { card ->
      groups.addAll(
        api.getGroups(card.id, token)
          .filter { it.nomSection != null }
          .map { it.toTable() }
      )
    }
    groups.forEach(groupsDao::insert)
    return groups.map { it.toModel() }
  }
}
