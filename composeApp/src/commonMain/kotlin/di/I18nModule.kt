package di

import mehiz.abdallah.progres.i18n.Localize
import org.kodein.di.DI.Module
import org.kodein.di.bindSingleton

val I18nModule: (Localize) -> Module = {
  Module("i18nModule") {
    bindSingleton { it }
  }
}
