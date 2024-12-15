package di

import org.koin.core.module.Module
import org.koin.dsl.module
import utils.CredentialManager
import utils.PlatformUtils

val ApplicationModule: (
  credentialManger: CredentialManager,
  platformUtils: PlatformUtils
) -> Module = { credentialManager, platformUtils ->
  module {
    single { platformUtils }
    single { credentialManager }
  }
}
