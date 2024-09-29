package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.AcademicDecisionDao
import mehiz.abdallah.progres.data.daos.AcademicPeriodDao
import mehiz.abdallah.progres.data.daos.BacGradeDao
import mehiz.abdallah.progres.data.daos.BacInfoDao
import mehiz.abdallah.progres.data.daos.CCGradeDao
import mehiz.abdallah.progres.data.daos.ExamGradesDao
import mehiz.abdallah.progres.data.daos.ExamScheduleDao
import mehiz.abdallah.progres.data.daos.GroupsDao
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.data.daos.SubjectScheduleDao
import mehiz.abdallah.progres.data.daos.SubjectsDao
import mehiz.abdallah.progres.data.daos.TranscriptDao
import mehiz.abdallah.progres.data.daos.TranscriptSubjectDao
import mehiz.abdallah.progres.data.daos.TranscriptUEDao
import mehiz.abdallah.progres.data.daos.UserAuthDao
import mehiz.abdallah.progres.data.db.AcademicDecisionTable
import mehiz.abdallah.progres.data.db.AcademicPeriodTable
import mehiz.abdallah.progres.data.db.BacGradeTable
import mehiz.abdallah.progres.data.db.BacInfoTable
import mehiz.abdallah.progres.data.db.CCGradeTable
import mehiz.abdallah.progres.data.db.ExamGradeTable
import mehiz.abdallah.progres.data.db.ExamScheduleTable
import mehiz.abdallah.progres.data.db.GroupTable
import mehiz.abdallah.progres.data.db.SubjectScheduleTable
import mehiz.abdallah.progres.data.db.SubjectTable
import mehiz.abdallah.progres.data.db.TranscriptSubjectTable
import mehiz.abdallah.progres.data.db.TranscriptTable
import mehiz.abdallah.progres.data.db.TranscriptUETable
import mehiz.abdallah.progres.domain.models.AcademicDecisionModel
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.BacInfoModel
import mehiz.abdallah.progres.domain.models.CCGradeModel
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import mehiz.abdallah.progres.domain.models.ExamScheduleModel
import mehiz.abdallah.progres.domain.models.GroupModel
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.SubjectModel
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import mehiz.abdallah.progres.domain.models.TranscriptModel
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
  private val bacInfoDao: BacInfoDao,
  private val ccGradeDao: CCGradeDao,
  private val subjectsDao: SubjectsDao,
  private val userAuthDao: UserAuthDao,
  private val bacGradeDao: BacGradeDao,
  private val examGradesDao: ExamGradesDao,
  private val studentCardDao: StudentCardDao,
  private val examScheduleDao: ExamScheduleDao,
  private val individualInfoDao: IndividualInfoDao,
  private val academicPeriodDao: AcademicPeriodDao,
  private val subjectScheduleDao: SubjectScheduleDao,
  private val transcriptDao: TranscriptDao,
  private val transcriptUEDao: TranscriptUEDao,
  private val transcriptSubjectDao: TranscriptSubjectDao,
  private val academicDecisionDao: AcademicDecisionDao,
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

  private suspend fun getBacInfo(): BacInfoTable {
    bacInfoDao.get()?.let {
      return it
    }
    return api.getBacInfo(uuid = userAuthDao.getUuid(), userAuthDao.getToken()).toTable().also {
      bacInfoDao.insert(it)
    }
  }

  private suspend fun getBacGrades(): List<BacGradeTable> {
    bacGradeDao.getAllBacGrades().let {
      if (it.isNotEmpty()) return it
    }
    return api.getBacNotes(userAuthDao.getUuid(), userAuthDao.getToken()).map {
      it.toTable()
    }.onEach(bacGradeDao::insert)
  }

  suspend fun getBacInfoWithGrades(): BacInfoModel {
    return getBacInfo().toModel(getBacGrades().map { it.toModel() })
  }

  suspend fun getAllTranscripts(): List<TranscriptModel> {
    val academicPeriods = getAcademicPeriods()
    transcriptDao.getAllTranscripts().let { transcripts ->
      if (transcripts.isEmpty()) return@let
      return transcripts.map { transcript ->
        val ues = transcriptUEDao.getUEsByTranscriptId(transcript.id)
        if (ues.isEmpty()) return@let
        transcript.toModel(
          period = academicPeriods.firstOrNull { it.id == transcript.periodId },
          ues.map { ue ->
            val subjects = transcriptSubjectDao.getSubjectsByUEId(ue.id)
            if (subjects.isEmpty()) return@let
            ue.toModel(subjects.map { subject -> subject.toModel() })
          },
        )
      }
    }
    val cards = getStudentCards()
    val token = userAuthDao.getToken()
    val uuid = userAuthDao.getUuid()
    val transcripts = mutableListOf<TranscriptTable>()
    val ues = mutableListOf<TranscriptUETable>()
    val subjects = mutableListOf<TranscriptSubjectTable>()
    cards.forEach { card ->
      api.getAcademicTranscripts(uuid, card.id, token).forEach { transcript ->
        transcripts.add(transcript.toTable())
        ues.addAll(transcript.bilanUes.map { it.toTable() })
        transcript.bilanUes.forEach {
          subjects.addAll(it.bilanMcs.map { it.toTable() })
        }
      }
    }
    return transcripts.map { transcript ->
      transcriptDao.insert(transcript)
      transcript.toModel(
        period = academicPeriods.firstOrNull { it.id == transcript.periodId },
        ues.mapNotNull { ue ->
          transcriptUEDao.insert(ue)
          if (ue.sessionId != transcript.id) {
            null
          } else {
            ue.toModel(
              subjects.mapNotNull {
                transcriptSubjectDao.insert(it)
                if (it.ueId != ue.id) null else it.toModel()
              },
            )
          }
        },
      )
    }
  }

  suspend fun getAllAcademicDecisions(): List<AcademicDecisionModel> {
    academicDecisionDao.getAllAcademicDecisions().let {
      if (it.isNotEmpty()) {
        val periods = getAcademicPeriods()
        return it.map { decision -> decision.toModel(periods.first { it.id == decision.periodId }) }
      }
    }
    val cards = getStudentCards()
    val uuid = userAuthDao.getUuid()
    val token = userAuthDao.getToken()
    val decisions = mutableListOf<AcademicDecisionTable>()
    val academicPeriods = getAcademicPeriods()
    cards.forEach { card ->
      api.getAcademicDecision(uuid, card.id, token)?.let {
        academicPeriods
          .firstOrNull { it.oofId == card.openingTrainingOfferId }?.id?.let { id ->
            it.toTable(id)
          }?.let(decisions::add)
      }
    }
    decisions.forEach(academicDecisionDao::insert)
    return decisions.map { decision -> decision.toModel(academicPeriods.first { it.id == decision.periodId }) }
  }

  suspend fun getAllCCGrades(): List<CCGradeModel> {
    val academicPeriods = getAcademicPeriods()
    ccGradeDao.getAllCCGrades().let { grades ->
      if (grades.isNotEmpty()) {
        return grades.map { grade ->
          grade.toModel(academicPeriods.first { it.id == grade.periodId })
        }
      }
    }
    val cards = getStudentCards()
    val token = userAuthDao.getToken()
    val grades = mutableListOf<CCGradeTable>()
    cards.forEach { card ->
      grades.addAll(
        api.getCCGrades(card.id, token).mapNotNull { grade ->
          academicPeriods.firstOrNull {
            grade.llPeriode == it.periodStringLatin && card.openingTrainingOfferId == it.oofId
          }?.id?.let { grade.toTable(it) }
        },
      )
    }
    return grades.map { grade ->
      ccGradeDao.insert(grade)
      grade.toModel(academicPeriods.first { it.id == grade.periodId })
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
    bacGradeDao.deleteAllBacGrades()
    bacInfoDao.delete()
    academicDecisionDao.deleteAllAcademicDecisions()
    transcriptDao.deleteAllTranscripts()
    transcriptUEDao.deleteAllUETranscripts()
    transcriptSubjectDao.deleteAllSubjects()
    ccGradeDao.deleteAllCCGrades()
    userAuthDao.deleteUserAuth()
  }
}
