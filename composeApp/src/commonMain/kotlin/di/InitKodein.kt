package di

import mehiz.abdallah.progres.domain.DomainModule
import org.kodein.di.DI

fun initKodein(
  datastorePath: String,
): DI {
  return DI.from(
    listOf(
      PreferencesModule(datastorePath),
      DomainModule
    )
  )
}

