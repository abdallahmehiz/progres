package mehiz.abdallah.progres.domain.di

import mehiz.abdallah.progres.api.ApiModule
import mehiz.abdallah.progres.data.database.DatabaseModule
import mehiz.abdallah.progres.domain.AcademicPeriodUseCase
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.BacInfoUseCase
import mehiz.abdallah.progres.domain.CCGradeUseCase
import mehiz.abdallah.progres.domain.ExamGradeUseCase
import mehiz.abdallah.progres.domain.ExamScheduleUseCase
import mehiz.abdallah.progres.domain.GroupUseCase
import mehiz.abdallah.progres.domain.StudentCardUseCase
import mehiz.abdallah.progres.domain.SubjectScheduleUseCase
import mehiz.abdallah.progres.domain.SubjectUseCase
import mehiz.abdallah.progres.domain.TranscriptUseCase
import mehiz.abdallah.progres.domain.UserAuthUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DomainModule = module {
  includes(
    ApiModule,
    DatabaseModule
  )

  singleOf(::AccountUseCase)
  singleOf(::ExamScheduleUseCase)
  singleOf(::AcademicPeriodUseCase)
  singleOf(::BacInfoUseCase)
  singleOf(::CCGradeUseCase)
  singleOf(::ExamGradeUseCase)
  singleOf(::GroupUseCase)
  singleOf(::StudentCardUseCase)
  singleOf(::SubjectScheduleUseCase)
  singleOf(::SubjectUseCase)
  singleOf(::TranscriptUseCase)
  singleOf(::UserAuthUseCase)
}
