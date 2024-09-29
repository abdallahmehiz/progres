package di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ui.home.HomeScreenViewModel
import ui.home.bacinfoscreen.BacInfoScreenViewModel
import ui.home.enrollments.EnrollmentsScreenViewModel
import ui.home.examgrades.ExamGradesViewModel
import ui.home.examsschedule.ExamsScheduleScreenViewModel
import ui.home.groups.GroupsViewModel
import ui.home.subjects.SubjectsScreenViewModel
import ui.home.subjectsschedule.SubjectsScheduleScreenViewModel
import ui.home.transcriptsscreen.TranscriptsScreenViewModel

val ViewModelsModule = module {
  viewModelOf(::HomeScreenViewModel)
  viewModelOf(::ExamGradesViewModel)
  viewModelOf(::EnrollmentsScreenViewModel)
  viewModelOf(::ExamsScheduleScreenViewModel)
  viewModelOf(::GroupsViewModel)
  viewModelOf(::SubjectsScreenViewModel)
  viewModelOf(::SubjectsScheduleScreenViewModel)
  viewModelOf(::BacInfoScreenViewModel)
  viewModelOf(::TranscriptsScreenViewModel)
}
