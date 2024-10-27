package di

import com.russhwolf.settings.ObservableSettings
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import preferences.BasePreferences
import preferences.preference.MultiplatformPreferenceStore
import preferences.preference.PreferenceStore

val PreferencesModule: (ObservableSettings) -> Module = { settings ->
  module {
    single { settings }
    singleOf(::MultiplatformPreferenceStore).bind(PreferenceStore::class)
    singleOf(::BasePreferences)
  }
}
