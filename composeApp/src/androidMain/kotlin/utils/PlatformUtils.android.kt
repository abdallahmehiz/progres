package utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource

actual class PlatformUtils(context: Context) {

  private val context = context
  actual val platform: Platform = Platform.Android

  actual fun openURI(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  }

  actual fun getString(res: StringResource, vararg args: Any): String {
    return context.getString(res.resourceId, *args)
  }

  actual fun getPlural(
    res: PluralsResource,
    amount: Int,
    vararg args: Any,
  ): String {
    return context.resources.getQuantityString(res.resourceId, amount, args)
  }

  actual fun copyTextToClipboard(text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text))
  }
}
