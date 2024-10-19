package utils

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource

expect class PlatformUtils {
  val platform: Platform
  val platformVersion: Int

  fun openURI(uri: String)

  fun copyTextToClipboard(text: String)

  fun getString(res: StringResource, vararg args: Any): String

  fun getPlural(res: PluralsResource, amount: Int, vararg args: Any): String

  fun downloadByteArray(byteArray: ByteArray, fileName: String, contentType: String)

  fun shareByteArray(byteArray: ByteArray, fileName: String, contentType: String)

  fun toast(text: String, length: ToastLength = ToastLength.Short)
}

enum class ToastLength {
  Short, Long
}

enum class Platform {
  Ios,
  Desktop, // soon
  Android,
}
