package mehiz.abdallah.progres.di

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module
import utils.AuthRefreshWorker
import utils.workers.AppUpdateCheckWorker
import utils.workers.CCGradesUpdateWorker
import utils.workers.ExamGradesUpdateWorker
import utils.workers.TranscriptsUpdateWorker

val WorkersModule = module {
  workerOf(::AuthRefreshWorker)
  workerOf(::CCGradesUpdateWorker)
  workerOf(::ExamGradesUpdateWorker)
  workerOf(::TranscriptsUpdateWorker)
  workerOf(::AppUpdateCheckWorker)
}
