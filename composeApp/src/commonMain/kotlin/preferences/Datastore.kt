package preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(producePath: () -> String): DataStore<Preferences> {
  return PreferenceDataStoreFactory.createWithPath { (producePath() + "/" + DATASTORE_FILENAME).toPath() }
}

internal const val DATASTORE_FILENAME = "prefs.preferences_pb"
