package di

import mehiz.abdallah.progres.i18n.Localize
import org.koin.core.module.Module
import org.koin.dsl.module

val I18nModule: (Localize) -> Module = {
  module {
    single { it }
  }
}
