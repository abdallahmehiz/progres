package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ApiModule
import mehiz.abdallah.progres.data.database.DatabaseModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DomainModule = module {
  includes(
    ApiModule,
    DatabaseModule
  )

  singleOf(::AccountUseCase)
}
