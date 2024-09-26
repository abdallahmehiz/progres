package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.AcademicPeriodDao
import mehiz.abdallah.progres.data.daos.ExamGradesDao
import mehiz.abdallah.progres.data.daos.ExamScheduleDao
import mehiz.abdallah.progres.data.daos.GroupsDao
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.data.daos.SubjectScheduleDao
import mehiz.abdallah.progres.data.daos.SubjectsDao
import mehiz.abdallah.progres.data.daos.UserAuthDao
import mehiz.abdallah.progres.data.db.AcademicPeriodTable
import mehiz.abdallah.progres.data.db.ExamGradeTable
import mehiz.abdallah.progres.data.db.ExamScheduleTable
import mehiz.abdallah.progres.data.db.GroupTable
import mehiz.abdallah.progres.data.db.SubjectScheduleTable
import mehiz.abdallah.progres.data.db.SubjectTable
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import mehiz.abdallah.progres.domain.models.ExamScheduleModel
import mehiz.abdallah.progres.domain.models.GroupModel
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.SubjectModel
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import mehiz.abdallah.progres.domain.models.toUserAuthTable
import kotlin.uuid.ExperimentalUuidApi

// Will be split up later...
@Suppress("LongParameterList", "TooManyFunctions")
@OptIn(ExperimentalUuidApi::class)
class AccountUseCase(
  private val api: ProgresApi,
  private val groupsDao: GroupsDao,
  private val subjectsDao: SubjectsDao,
  private val userAuthDao: UserAuthDao,
  private val examGradesDao: ExamGradesDao,
  private val studentCardDao: StudentCardDao,
  private val examScheduleDao: ExamScheduleDao,
  private val individualInfoDao: IndividualInfoDao,
  private val academicPeriodDao: AcademicPeriodDao,
  private val subjectScheduleDao: SubjectScheduleDao,
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
        api.getGroups(card.id, token).filter { it.nomSection != null }.map { it.toTable() },
      )
    }
    groups.forEach(groupsDao::insert)
    return groups.map { it.toModel() }
  }

  suspend fun getAllSubjects(): List<SubjectModel> {
    subjectsDao.getAllSubjects().let {
      if (it.isNotEmpty()) return it.map { it.toModel() }
    }
    val studentCards = getStudentCards()
    val token = userAuthDao.getToken()
    val subjects = mutableListOf<SubjectTable>()
    studentCards.forEach { card ->
      subjects.addAll(api.getSubjects(card.openingTrainingOfferId, card.levelId, token).map { it.toTable() })
    }
    return subjects.map {
      subjectsDao.insert(it)
      it.toModel()
    }
  }

  private suspend fun getAcademicPeriods(refresh: Boolean = false): List<AcademicPeriodModel> {
    academicPeriodDao.getAllAcademicPeriods().let {
      if (it.isNotEmpty() && !refresh) return it.map { it.toModel() }
    }
    val studentCards = getStudentCards()
    val subjects = getAllSubjects()
    return subjects.distinctBy { it.periodId }.map { subject ->
      val relevantCard = studentCards.first { it.openingTrainingOfferId == subject.oofId }
      AcademicPeriodTable(
        id = subject.periodId,
        periodStringLatin = subject.periodStringLatin,
        periodStringArabic = subject.periodStringArabic,
        oofId = subject.oofId,
        academicYearId = relevantCard.levelId,
        academicYearStringLatin = relevantCard.levelStringLongLatin,
        academicYearStringArabic = relevantCard.levelStringLongArabic,
      )
    }.map {
      academicPeriodDao.insert(it)
      it.toModel()
    }
  }

  suspend fun getAllSubjectsSchedule(): List<SubjectScheduleModel> {
    val academicPeriods = getAcademicPeriods()
    subjectScheduleDao.getAllSchedules().let {
      if (it.isNotEmpty()) {
        return it.mapNotNull { subject ->
          val period = academicPeriods.firstOrNull { it.id == subject.periodId }
            ?: getAcademicPeriods(true).firstOrNull { it.id == subject.periodId }
          period?.let { subject.toModel(it) }
        }
      }
    }
    val studentCards = getStudentCards()
    val token = userAuthDao.getToken()
    val subjectsSchedules = mutableListOf<SubjectScheduleTable>()
    studentCards.forEach { card ->
      subjectsSchedules.addAll(api.getSubjectsSchedule(card.id, token).map { it.toTable() })
    }
    return subjectsSchedules.mapNotNull { subject ->
      subjectScheduleDao.insert(subject)
      val period = academicPeriods.firstOrNull { it.id == subject.periodId }
        ?: getAcademicPeriods(true).firstOrNull { it.id == subject.periodId }
      period?.let { subject.toModel(it) }
    }
  }

  suspend fun logout() {
    groupsDao.deleteAllGroups()
    academicPeriodDao.deleteAllAcademicPeriods()
    examScheduleDao.deleteAllExamSchedules()
    examGradesDao.deleteAllExamGrades()
    subjectScheduleDao.deleteAllSchedules()
    subjectsDao.deleteAllSubjects()
    studentCardDao.deleteAllCards()
    individualInfoDao.deleteAllIndividualInfo()
    userAuthDao.deleteUserAuth()
  }
}
