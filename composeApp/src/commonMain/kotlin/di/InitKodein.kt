package di

import mehiz.abdallah.progres.domain.DomainModule
import mehiz.abdallah.progres.i18n.Localize
import org.kodein.di.DI

fun initKodein(
  datastorePath: String,
  localize: Localize
): DI {
  return DI.from(
    listOf(
      PreferencesModule(datastorePath),
      DomainModule,
      I18nModule(localize)
    )
  )
}

