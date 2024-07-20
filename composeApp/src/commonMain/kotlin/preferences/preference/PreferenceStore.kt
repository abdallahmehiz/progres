package preferences.preference

interface PreferenceStore {
  fun getString(key: String, defaultValue: String = ""): Preference<String>

  fun getLong(key: String, defaultValue: Long = 0): Preference<Long>

  fun getInt(key: String, defaultValue: Int = 0): Preference<Int>

  fun getFloat(key: String, defaultValue: Float = 0f): Preference<Float>

  fun getBoolean(key: String, defaultValue: Boolean = false): Preference<Boolean>

  fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Preference<Set<String>>

  fun getByteArray(key: String, defaultValue: ByteArray = byteArrayOf()): Preference<ByteArray>
}
