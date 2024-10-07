package di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ui.home.HomeScreenModel
import ui.home.bacinfo.BacInfoScreenModel
import ui.home.ccgrades.CCGradesScreenModel
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
  // i know this causes a memory leak cause it's only used ONCE in the entire app's life
  // but it's the simplest solution i found to keep a coroutine from getting cancelled after a screen gets popped
  // and since it contains no data, it's a price i am willing to pay, until i find a better solution
}
