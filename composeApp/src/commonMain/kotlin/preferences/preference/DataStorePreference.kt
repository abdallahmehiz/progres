package preferences.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

sealed class DataStorePreference<T>(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: T,
) : Preference<T> {
  abstract fun read(key: Preferences.Key<T>): T

  abstract fun write(key: Preferences.Key<T>, value: T)

  abstract val key: Preferences.Key<T>

  val coroutineScope = CoroutineScope(Dispatchers.IO)

  override fun key(): String {
    return key.name
  }

  override fun get(): T {
    return read(key)
  }

  override fun set(value: T) {
    write(key, value)
  }

  override fun isSet(): Boolean {
    return runBlocking { dataStore.data.map { it.contains(key) }.first() }
  }

  override fun delete() {
    coroutineScope.launch { dataStore.edit { it.remove(key) } }
  }

  override fun defaultValue(): T {
    return defaultValue
  }

  override fun changes(): Flow<T> {
    return dataStore.data.map { it[key] ?: defaultValue }
  }

  override fun stateIn(scope: CoroutineScope): StateFlow<T> {
    return changes().stateIn(scope, SharingStarted.Eagerly, get())
  }
}

class StringPrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: String,
) : DataStorePreference<String>(dataStore, key, defaultValue) {
  override val key = stringPreferencesKey(key)

  override fun read(key: Preferences.Key<String>): String {
    return runBlocking {
      dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue
    }
  }

  override fun write(key: Preferences.Key<String>, value: String) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}

class IntPrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: Int
) : DataStorePreference<Int>(dataStore, key, defaultValue) {
  override val key: Preferences.Key<Int> = intPreferencesKey(key)

  override fun read(key: Preferences.Key<Int>): Int {
    return runBlocking { dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue }
  }

  override fun write(key: Preferences.Key<Int>, value: Int) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}

class FloatPrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: Float,
) : DataStorePreference<Float>(dataStore, key, defaultValue) {
  override val key = floatPreferencesKey(key)

  override fun read(key: Preferences.Key<Float>): Float {
    return runBlocking { dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue }
  }

  override fun write(key: Preferences.Key<Float>, value: Float) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}

class DoublePrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: Double,
) : DataStorePreference<Double>(dataStore, key, defaultValue) {
  override val key = doublePreferencesKey(key)

  override fun read(key: Preferences.Key<Double>): Double {
    return runBlocking { dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue }
  }

  override fun write(key: Preferences.Key<Double>, value: Double) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}

class LongPrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: Long,
) : DataStorePreference<Long>(dataStore, key, defaultValue) {
  override val key = longPreferencesKey(key)

  override fun read(key: Preferences.Key<Long>): Long {
    return runBlocking { dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue }
  }

  override fun write(key: Preferences.Key<Long>, value: Long) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}

class BooleanPrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: Boolean,
) : DataStorePreference<Boolean>(dataStore, key, defaultValue) {
  override val key = booleanPreferencesKey(key)

  override fun read(key: Preferences.Key<Boolean>): Boolean {
    return runBlocking { dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue }
  }

  override fun write(key: Preferences.Key<Boolean>, value: Boolean) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}

class StringSetPrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: Set<String>,
) : DataStorePreference<Set<String>>(dataStore, key, defaultValue) {
  override val key = stringSetPreferencesKey(key)

  override fun read(key: Preferences.Key<Set<String>>): Set<String> {
    return runBlocking { dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue }
  }

  override fun write(key: Preferences.Key<Set<String>>, value: Set<String>) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}

class ByteArrayPrimitive(
  private val dataStore: DataStore<Preferences>,
  key: String,
  private val defaultValue: ByteArray,
) : DataStorePreference<ByteArray>(dataStore, key, defaultValue) {
  override val key = byteArrayPreferencesKey(key)

  override fun read(key: Preferences.Key<ByteArray>): ByteArray {
    return runBlocking { dataStore.data.map { it[key] }.firstOrNull() ?: defaultValue }
  }

  override fun write(key: Preferences.Key<ByteArray>, value: ByteArray) {
    coroutineScope.launch { dataStore.edit { it[key] = value } }
  }
}
