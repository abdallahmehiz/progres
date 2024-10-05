package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.data.daos.AcademicDecisionDao
import mehiz.abdallah.progres.data.daos.AcademicPeriodDao
import mehiz.abdallah.progres.data.daos.AccommodationStateDao
import mehiz.abdallah.progres.data.daos.BacGradeDao
import mehiz.abdallah.progres.data.daos.BacInfoDao
import mehiz.abdallah.progres.data.daos.CCGradeDao
import mehiz.abdallah.progres.data.daos.ExamGradeDao
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

@Suppress("LongParameterList")
class AccountUseCase(
  private val groupsDao: GroupsDao,
  private val bacInfoDao: BacInfoDao,
  private val ccGradeDao: CCGradeDao,
  private val subjectsDao: SubjectsDao,
  private val userAuthDao: UserAuthDao,
  private val bacGradeDao: BacGradeDao,
  private val examGradeDao: ExamGradeDao,
  private val studentCardDao: StudentCardDao,
  private val examScheduleDao: ExamScheduleDao,
  private val individualInfoDao: IndividualInfoDao,
  private val academicPeriodDao: AcademicPeriodDao,
  private val subjectScheduleDao: SubjectScheduleDao,
  private val transcriptDao: TranscriptDao,
  private val transcriptUEDao: TranscriptUEDao,
  private val transcriptSubjectDao: TranscriptSubjectDao,
  private val academicDecisionDao: AcademicDecisionDao,
  private val accommodationStateDao: AccommodationStateDao,
) {
  suspend fun logout() {
    groupsDao.deleteAllGroups()
    academicPeriodDao.deleteAllAcademicPeriods()
    examScheduleDao.deleteAllExamSchedules()
    examGradeDao.deleteAllExamGrades()
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
    accommodationStateDao.deleteAllAccommodationStates()
    userAuthDao.deleteUserAuth()
  }
}
