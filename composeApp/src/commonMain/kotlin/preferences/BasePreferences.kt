package preferences

import preferences.preference.PreferenceStore

class BasePreferences(
  preferences: PreferenceStore
) {
  val isLoggedIn = preferences.getBoolean("is_logged_in")
}
