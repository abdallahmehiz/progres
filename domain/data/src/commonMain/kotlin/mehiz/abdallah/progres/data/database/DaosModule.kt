package mehiz.abdallah.progres.data.database

import mehiz.abdallah.progres.data.daos.AcademicPeriodDao
import mehiz.abdallah.progres.data.daos.BacGradeDao
import mehiz.abdallah.progres.data.daos.BacInfoDao
import mehiz.abdallah.progres.data.daos.ExamGradesDao
import mehiz.abdallah.progres.data.daos.ExamScheduleDao
import mehiz.abdallah.progres.data.daos.GroupsDao
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.data.daos.SubjectScheduleDao
import mehiz.abdallah.progres.data.daos.SubjectsDao
import mehiz.abdallah.progres.data.daos.UserAuthDao
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DaosModule = module {
  singleOf(::UserAuthDao)
  singleOf(::StudentCardDao)
  singleOf(::IndividualInfoDao)
  singleOf(::ExamGradesDao)
  singleOf(::ExamScheduleDao)
  singleOf(::GroupsDao)
  singleOf(::SubjectsDao)
  singleOf(::SubjectScheduleDao)
  singleOf(::AcademicPeriodDao)
  singleOf(::BacInfoDao)
  singleOf(::BacGradeDao)
}
