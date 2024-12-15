package di

import mehiz.abdallah.progres.domain.di.DomainModule
import org.koin.dsl.module
import utils.CredentialManager
import utils.PlatformUtils

fun initKoin(
  datastorePath: String,
  credentialManager: CredentialManager,
  platformUtils: PlatformUtils,
) = module {
  includes(
    PreferencesModule(datastorePath),
    DomainModule,
    ScreenModelsModule,
    ApplicationModule(credentialManager, platformUtils)
  )
}
