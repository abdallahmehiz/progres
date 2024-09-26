package di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import preferences.BasePreferences
import preferences.createDataStore
import preferences.preference.DataStorePreferenceStore
import preferences.preference.PreferenceStore

val PreferencesModule: (String) -> Module = { dataStorePath ->
  module {
    single { createDataStore { dataStorePath } }
    singleOf(::DataStorePreferenceStore).bind(PreferenceStore::class)
    singleOf(::BasePreferences)
  }
}
