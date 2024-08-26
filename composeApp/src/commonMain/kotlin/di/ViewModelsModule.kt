package di

import org.kodein.di.DI
import org.kodein.di.bindSingletonOf
import ui.home.HomeScreenViewModel

val ViewModelsModule = DI.Module("ViewModelModule") {
  bindSingletonOf(::HomeScreenViewModel)
}
