package di

import mehiz.abdallah.progres.domain.DomainModule
import mehiz.abdallah.progres.i18n.Localize
import org.koin.dsl.module

fun initKoin(
  datastorePath: String,
  localize: Localize,
) = module {
  includes(
    PreferencesModule(datastorePath),
    DomainModule,
    I18nModule(localize),
    ViewModelsModule,
  )
}
