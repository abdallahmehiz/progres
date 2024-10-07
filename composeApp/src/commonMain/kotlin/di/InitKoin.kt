package di

import com.liftric.kvault.KVault
import mehiz.abdallah.progres.domain.di.DomainModule
import mehiz.abdallah.progres.i18n.Localize
import org.koin.dsl.module
import utils.CredentialManager

fun initKoin(
  datastorePath: String,
  localize: Localize,
  credentialManager: CredentialManager,
  kVault: KVault,
) = module {
  includes(
    PreferencesModule(datastorePath),
    DomainModule,
    I18nModule(localize),
    ScreenModelsModule,
    ApplicationModule(credentialManager, kVault)
  )
}
