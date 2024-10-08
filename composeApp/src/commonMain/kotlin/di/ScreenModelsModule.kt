package di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ui.home.HomeScreenModel
import ui.home.accommodation.AccommodationScreenModel
import ui.home.bacinfo.BacInfoScreenModel
import ui.home.ccgrades.CCGradesScreenModel
import ui.home.discharge.DischargeScreenModel
import ui.home.enrollments.EnrollmentsScreenModel
import ui.home.examgrades.ExamGradesScreenModel
import ui.home.examsschedule.ExamsScheduleScreenModel
import ui.home.groups.GroupsScreenModel
import ui.home.subjects.SubjectsScreenModel
import ui.home.subjectsschedule.SubjectsScheduleScreenModel
import ui.home.transcripts.TranscriptsScreenModel

val ScreenModelsModule = module {
  factoryOf(::HomeScreenModel)
  factoryOf(::ExamGradesScreenModel)
  factoryOf(::EnrollmentsScreenModel)
  factoryOf(::ExamsScheduleScreenModel)
  factoryOf(::GroupsScreenModel)
  factoryOf(::SubjectsScreenModel)
  factoryOf(::SubjectsScheduleScreenModel)
  factoryOf(::BacInfoScreenModel)
  factoryOf(::TranscriptsScreenModel)
  factoryOf(::CCGradesScreenModel)
  factoryOf(::DischargeScreenModel)
  factoryOf(::AccommodationScreenModel)
}
