package di

import com.liftric.kvault.KVault
import org.koin.core.module.Module
import org.koin.dsl.module
import utils.CredentialManager

val ApplicationModule: (
  credentialManger: CredentialManager,
  kvault: KVault,
) -> Module = { credentialManager, kvault ->
  module {
    single { credentialManager }
    single { kvault }
  }
}
