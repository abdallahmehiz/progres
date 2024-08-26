package di

import mehiz.abdallah.progres.domain.DomainModule
import mehiz.abdallah.progres.i18n.Localize
import org.kodein.di.DI

fun initKodein(
  datastorePath: String,
  localize: Localize,
  applicationContext: DI.Module? = null,
): DI {
  return DI.from(
    buildList {
      applicationContext?.let { add(it) }
      add(PreferencesModule(datastorePath))
      add(DomainModule)
      add(I18nModule(localize))
      add(ViewModelsModule)
    },
  )
}
