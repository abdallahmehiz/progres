package mehiz.abdallah.progres.di

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module
import utils.AuthRefreshWorker

val WorkersModule = module {
  workerOf(::AuthRefreshWorker)
}
