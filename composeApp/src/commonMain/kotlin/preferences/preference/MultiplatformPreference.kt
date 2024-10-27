package preferences.preference

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getDoubleFlow
import com.russhwolf.settings.coroutines.getFloatFlow
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.getLongFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed class Multiplatform<T>(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: T,
) : Preference<T> {
  abstract fun read(key: String): T

  abstract fun write(key: String, value: T)

  override fun key(): String {
    return key
  }

  override fun get(): T {
    return read(key)
  }

  override fun set(value: T) {
    write(key, value)
  }

  override fun isSet(): Boolean {
    return preferences.hasKey(key)
  }

  override fun delete() {
    preferences.remove(key)
  }

  override fun defaultValue(): T {
    return defaultValue
  }

  abstract override fun changes(): Flow<T>

  override fun stateIn(scope: CoroutineScope): StateFlow<T> {
    return changes().stateIn(scope, SharingStarted.Eagerly, get())
  }
}

class StringPrimitive(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: String,
) : Multiplatform<String>(preferences, key, defaultValue) {

  override fun read(key: String): String {
    return preferences[key] ?: defaultValue
  }

  @OptIn(ExperimentalSettingsApi::class)
  override fun changes(): Flow<String> {
    return preferences.getStringFlow(key, defaultValue)
  }

  override fun write(key: String, value: String) {
    preferences[key] = value
  }
}

class IntPrimitive(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: Int
) : Multiplatform<Int>(preferences, key, defaultValue) {

  override fun read(key: String): Int {
    return preferences[key] ?: defaultValue
  }

  @OptIn(ExperimentalSettingsApi::class)
  override fun changes(): Flow<Int> {
    return preferences.getIntFlow(key, defaultValue)
  }

  override fun write(key: String, value: Int) {
    preferences[key] = value
  }
}

class FloatPrimitive(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: Float
) : Multiplatform<Float>(preferences, key, defaultValue) {

  override fun read(key: String): Float {
    return preferences[key] ?: defaultValue
  }

  override fun write(key: String, value: Float) {
    preferences[key] = value
  }

  @OptIn(ExperimentalSettingsApi::class)
  override fun changes(): Flow<Float> {
    return preferences.getFloatFlow(key, defaultValue)
  }
}

class DoublePrimitive(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: Double
) : Multiplatform<Double>(preferences, key, defaultValue) {

  override fun read(key: String): Double {
    return preferences[key] ?: defaultValue
  }

  override fun write(key: String, value: Double) {
    preferences[key] = value
  }

  @OptIn(ExperimentalSettingsApi::class)
  override fun changes(): Flow<Double> {
    return preferences.getDoubleFlow(key, defaultValue)
  }
}

class LongPrimitive(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: Long
) : Multiplatform<Long>(preferences, key, defaultValue) {

  override fun read(key: String): Long {
    return preferences[key] ?: defaultValue
  }

  override fun write(key: String, value: Long) {
    preferences[key] = value
  }

  @OptIn(ExperimentalSettingsApi::class)
  override fun changes(): Flow<Long> {
    return preferences.getLongFlow(key, defaultValue)
  }
}

class BooleanPrimitive(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: Boolean
) : Multiplatform<Boolean>(preferences, key, defaultValue) {

  override fun read(key: String): Boolean {
    return preferences[key] ?: defaultValue
  }

  override fun write(key: String, value: Boolean) {
    preferences[key] = value
  }

  @OptIn(ExperimentalSettingsApi::class)
  override fun changes(): Flow<Boolean> {
    return preferences.getBooleanFlow(key, defaultValue)
  }
}

class Object<T>(
  private val preferences: ObservableSettings,
  private val key: String,
  private val defaultValue: T,
  private val serializer: (T) -> String,
  private val deserializer: (String) -> T
) : Multiplatform<T>(preferences, key, defaultValue) {
  override fun read(key: String): T {
    return preferences.getStringOrNull(key)?.let(deserializer) ?: defaultValue
  }

  override fun write(key: String, value: T) {
    preferences[key] = serializer(value)
  }

  @OptIn(ExperimentalSettingsApi::class)
  override fun changes(): Flow<T> {
    return preferences.getStringFlow(key, serializer(defaultValue)).map { value ->
      deserializer(value)
    }
  }
}
