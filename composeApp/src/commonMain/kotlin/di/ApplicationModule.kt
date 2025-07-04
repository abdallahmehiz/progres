package di

import org.koin.core.module.Module
import org.koin.dsl.module
import utils.CredentialManager
import utils.FileStorageManager
import utils.PlatformUtils

val ApplicationModule: (
  credentialManger: CredentialManager,
  platformUtils: PlatformUtils,
  dataPath: String
) -> Module = { credentialManager, platformUtils, dataPath ->
  module {
    single { platformUtils }
    single { credentialManager }
    single { FileStorageManager(dataPath) }
  }
}
