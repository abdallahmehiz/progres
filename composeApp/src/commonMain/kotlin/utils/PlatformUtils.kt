package utils

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource

expect class PlatformUtils {
  val platform: Platform

  fun openURI(uri: String)

  fun copyTextToClipboard(text: String)

  fun getString(res: StringResource, vararg args: Any): String

  fun getPlural(res: PluralsResource, amount: Int, vararg args: Any): String
}

enum class Platform {
  Ios,
  Desktop, // soon
  Android,
}
