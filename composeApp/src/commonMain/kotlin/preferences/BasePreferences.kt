package preferences

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import mehiz.abdallah.progres.i18n.MR
import preferences.preference.PreferenceStore
import preferences.preference.getEnum
import presentation.theme.DarkMode

class BasePreferences(
  preferences: PreferenceStore
) {
  val isLoggedIn = preferences.getBoolean("is_logged_in")

  val darkMode = preferences.getEnum("dark_mode", DarkMode.System)
  val materialYou = preferences.getBoolean("material_you")
  val language = preferences.getEnum("lang", Language.System)
}

enum class Language(val locale: StringDesc.LocaleType, val string: StringResource) {
  System(StringDesc.LocaleType.System, MR.strings.lang_sys),
  English(StringDesc.LocaleType.Custom("en"), MR.strings.lang_en),

  // Arabic(StringDesc.LocaleType.Custom("ar"), MR.strings.lang_ar),
  French(StringDesc.LocaleType.Custom("fr"), MR.strings.lang_fr),
}
