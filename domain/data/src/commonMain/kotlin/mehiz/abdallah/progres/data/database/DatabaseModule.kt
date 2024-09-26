package mehiz.abdallah.progres.data.database

import org.koin.core.module.Module
import org.koin.dsl.module

val DatabaseModule = module {
  includes(
    ProgresDatabaseModule,
    DaosModule
  )
}

expect val ProgresDatabaseModule: Module
