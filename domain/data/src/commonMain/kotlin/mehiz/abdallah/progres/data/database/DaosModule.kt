package mehiz.abdallah.progres.data.database

import mehiz.abdallah.progres.data.daos.AcademicDecisionDao
import mehiz.abdallah.progres.data.daos.AcademicPeriodDao
import mehiz.abdallah.progres.data.daos.AccommodationDao
import mehiz.abdallah.progres.data.daos.BacGradeDao
import mehiz.abdallah.progres.data.daos.BacInfoDao
import mehiz.abdallah.progres.data.daos.CCGradeDao
import mehiz.abdallah.progres.data.daos.DischargeDao
import mehiz.abdallah.progres.data.daos.EstablishmentDao
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DaosModule = module {
  singleOf(::UserAuthDao)
  singleOf(::AccommodationDao)
  singleOf(::EstablishmentDao)
  singleOf(::StudentCardDao)
  singleOf(::IndividualInfoDao)
  singleOf(::ExamGradeDao)
  singleOf(::ExamScheduleDao)
  singleOf(::GroupsDao)
  singleOf(::SubjectsDao)
  singleOf(::SubjectScheduleDao)
  singleOf(::AcademicPeriodDao)
  singleOf(::BacInfoDao)
  singleOf(::BacGradeDao)
  singleOf(::AcademicDecisionDao)
  singleOf(::TranscriptDao)
  singleOf(::TranscriptUEDao)
  singleOf(::TranscriptSubjectDao)
  singleOf(::CCGradeDao)
  singleOf(::DischargeDao)
}
