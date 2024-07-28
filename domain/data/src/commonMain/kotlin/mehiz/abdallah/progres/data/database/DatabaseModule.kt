package mehiz.abdallah.progres.data.database

import org.kodein.di.DI

val DatabaseModule: DI.Module = DI.Module("DatabaseDriver") {
  import(ProgresDatabaseModule)
  import(DaosModule)
}

expect val ProgresDatabaseModule: DI.Module
