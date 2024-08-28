package preferences

import preferences.preference.PreferenceStore
import preferences.preference.getEnum
import presentation.theme.DarkMode

class BasePreferences(
  preferences: PreferenceStore
) {
  val isLoggedIn = preferences.getBoolean("is_logged_in")

  val darkMode = preferences.getEnum("dark_mode", DarkMode.System)
  val materialYou = preferences.getBoolean("material_you")
}
