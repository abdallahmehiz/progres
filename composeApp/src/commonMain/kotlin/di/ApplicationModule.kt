package di

import org.koin.core.module.Module
import org.koin.dsl.module
import utils.CredentialManager

val ApplicationModule: (CredentialManager) -> Module = { accountManager ->
  module {
    single { accountManager }
  }
}
