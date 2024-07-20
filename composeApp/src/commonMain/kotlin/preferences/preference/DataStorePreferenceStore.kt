package preferences.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import preferences.preference.BooleanPrimitive
import preferences.preference.ByteArrayPrimitive
import preferences.preference.IntPrimitive
import preferences.preference.LongPrimitive
import preferences.preference.Preference
import preferences.preference.PreferenceStore
import preferences.preference.StringPrimitive
import preferences.preference.StringSetPrimitive

class DataStorePreferenceStore(
  private val dataStore: DataStore<Preferences>
):PreferenceStore {
  override fun getString(key: String, defaultValue: String): Preference<String> {
    return StringPrimitive(dataStore, key, defaultValue)
  }

  override fun getLong(key: String, defaultValue: Long): Preference<Long> {
    return LongPrimitive(dataStore, key, defaultValue)
  }

  override fun getInt(key: String, defaultValue: Int): Preference<Int> {
    return IntPrimitive(dataStore, key, defaultValue)
  }

  override fun getFloat(key: String, defaultValue: Float): Preference<Float> {
    return getFloat(key, defaultValue)
  }

  override fun getBoolean(key: String, defaultValue: Boolean): Preference<Boolean> {
    return BooleanPrimitive(dataStore, key, defaultValue)
  }

  override fun getStringSet(key: String, defaultValue: Set<String>): Preference<Set<String>> {
    return StringSetPrimitive(dataStore, key, defaultValue)
  }

  override fun getByteArray(key: String, defaultValue: ByteArray): Preference<ByteArray> {
    return ByteArrayPrimitive(dataStore, key, defaultValue)
  }
}
