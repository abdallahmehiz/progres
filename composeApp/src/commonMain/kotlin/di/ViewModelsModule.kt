package di

import org.kodein.di.DI
import org.kodein.di.bindSingletonOf
import ui.home.HomeScreenViewModel
import ui.home.enrollments.EnrollmentsScreenViewModel
import ui.home.examgrades.ExamGradesViewModel
import ui.home.examsschedule.ExamsScheduleScreenViewModel

val ViewModelsModule = DI.Module("ViewModelModule") {
  bindSingletonOf(::HomeScreenViewModel)
  bindSingletonOf(::ExamGradesViewModel)
  bindSingletonOf(::EnrollmentsScreenViewModel)
  bindSingletonOf(::ExamsScheduleScreenViewModel)
}
