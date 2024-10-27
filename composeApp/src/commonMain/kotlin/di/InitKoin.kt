package di

import com.liftric.kvault.KVault
import com.russhwolf.settings.ObservableSettings
import mehiz.abdallah.progres.domain.di.DomainModule
import org.koin.dsl.module
import utils.CredentialManager
import utils.PlatformUtils

fun initKoin(
  settings: ObservableSettings,
  credentialManager: CredentialManager,
  kVault: KVault,
  platformUtils: PlatformUtils,
) = module {
  includes(
    PreferencesModule(settings),
    DomainModule,
    ScreenModelsModule,
    ApplicationModule(credentialManager, kVault, platformUtils)
  )
}
