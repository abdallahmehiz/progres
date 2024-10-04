package di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ui.home.HomeScreenModel
import ui.home.bacinfoscreen.BacInfoScreenModel
import ui.home.ccgradesscreen.CCGradesScreenModel
import ui.home.enrollments.EnrollmentsScreenModel
import ui.home.examgrades.ExamGradesScreenModel
import ui.home.examsschedule.ExamsScheduleScreenModel
import ui.home.groups.GroupsScreenModel
import ui.home.subjects.SubjectsScreenModel
import ui.home.subjectsschedule.SubjectsScheduleScreenModel
import ui.home.transcriptsscreen.TranscriptsScreenModel

val ViewModelsModule = module {
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
}
