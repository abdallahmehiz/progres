package di

import org.kodein.di.DI

fun initKodein(
  datastorePath: String,
): DI {
  return DI.from(
    listOf(
      PreferencesModule(datastorePath)
    )
  )
}

