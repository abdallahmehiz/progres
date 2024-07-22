package di

import org.kodein.di.DI.Module
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import preferences.BasePreferences
import preferences.createDataStore
import preferences.preference.DataStorePreferenceStore
import preferences.preference.PreferenceStore

val PreferencesModule: (String) -> Module = {
  Module("PreferencesModule") {
    bindSingleton { createDataStore { it } }
    bindSingleton<PreferenceStore> { DataStorePreferenceStore(instance()) }
    bindSingleton { BasePreferences(instance()) }
  }
}
