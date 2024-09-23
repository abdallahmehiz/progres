package ui.crash

import android.os.Build
import mehiz.abdallah.progres.BuildKonfig.BuildKonfig
import java.io.BufferedReader
import java.io.InputStreamReader

actual fun collectLogs(): String {
  val process = Runtime.getRuntime()
  val reader = BufferedReader(InputStreamReader(process.exec("logcat -d").inputStream))
  val logcat = StringBuilder()
  reader.readLines().forEach(logcat::appendLine)
  // clear logcat so it doesn't pollute subsequent crashes
  process.exec("logcat -c")
  return logcat.toString()
}

actual fun collectDeviceInfo(): String {
  return """
    App version: ${BuildKonfig.VERSION_NAME}
    Android version: ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})
    Device brand: ${Build.BRAND}
    Device manufacturer: ${Build.MANUFACTURER}
    Device model: ${Build.MODEL} (${Build.DEVICE})
  """.trimIndent()
}
