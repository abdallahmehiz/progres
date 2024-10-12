package di

import com.liftric.kvault.KVault
import org.koin.core.module.Module
import org.koin.dsl.module
import utils.CredentialManager
import utils.PlatformUtils

val ApplicationModule: (
  credentialManger: CredentialManager,
  kvault: KVault,
  platformUtils: PlatformUtils
) -> Module = { credentialManager, kvault, platformUtils ->
  module {
    single { platformUtils }
    single { credentialManager }
    single { kvault }
  }
}
