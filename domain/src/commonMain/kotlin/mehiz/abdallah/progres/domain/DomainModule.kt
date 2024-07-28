package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ApiModule
import mehiz.abdallah.progres.data.database.DatabaseModule
import org.kodein.di.DI.Module
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val DomainModule: Module = Module("DomainModule") {
  import(ApiModule)
  import(DatabaseModule)

  bindSingleton { AccountUseCase(instance(), instance()) }
}
