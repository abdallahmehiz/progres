package preferences.preference

import com.russhwolf.settings.ObservableSettings

class MultiplatformPreferenceStore(
  private val preference: ObservableSettings,
) : PreferenceStore {
  override fun getString(key: String, defaultValue: String): Preference<String> {
    return StringPrimitive(preference, key, defaultValue)
  }

  override fun getLong(key: String, defaultValue: Long): Preference<Long> {
    return LongPrimitive(preference, key, defaultValue)
  }

  override fun getInt(key: String, defaultValue: Int): Preference<Int> {
    return IntPrimitive(preference, key, defaultValue)
  }

  override fun getFloat(key: String, defaultValue: Float): Preference<Float> {
    return FloatPrimitive(preference, key, defaultValue)
  }

  override fun getBoolean(key: String, defaultValue: Boolean): Preference<Boolean> {
    return BooleanPrimitive(preference, key, defaultValue)
  }

  override fun getDouble(key: String, defaultValue: Double): Preference<Double> {
    return DoublePrimitive(preference, key, defaultValue)
  }

  override fun <T> getObject(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T,
  ): Preference<T> {
    return Object(
      preference,
      key,
      defaultValue,
      serializer,
      deserializer
    )
  }
}
