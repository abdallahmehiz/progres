package preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(producePath: () -> String): DataStore<Preferences> {
  return PreferenceDataStoreFactory.createWithPath { (producePath() + "/" + DataStoreFileName).toPath() }
}

internal const val DataStoreFileName = "prefs.preferences_pb"
