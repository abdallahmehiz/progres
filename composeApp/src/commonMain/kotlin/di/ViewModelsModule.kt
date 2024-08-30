package di

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import ui.home.HomeScreenViewModel
import ui.home.enrollments.EnrollmentsScreen
import ui.home.enrollments.EnrollmentsScreenViewModel
import ui.home.examgrades.ExamGradesViewModel

val ViewModelsModule = DI.Module("ViewModelModule") {
  bindSingletonOf(::HomeScreenViewModel)
  bindSingletonOf(::ExamGradesViewModel)
  bindSingletonOf(::EnrollmentsScreenViewModel)
}
