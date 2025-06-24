package utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.format
import mehiz.abdallah.progres.BuildConfig
import mehiz.abdallah.progres.i18n.MR
import java.io.File

actual class PlatformUtils(private var context: Context) {

  actual val platform: Platform = Platform.Android
  actual val platformVersion: Int = Build.VERSION.SDK_INT

  fun updateContext(context: Context) {
    this.context = context
  }

  actual fun openURI(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  }

  actual fun getString(res: StringResource, vararg args: Any): String {
    return res.format(args).toString(context)
  }

  actual fun getPlural(
    res: PluralsResource,
    amount: Int,
    vararg args: Any,
  ): String {
    return res.format(amount, args).toString(context)
  }

  actual fun copyTextToClipboard(text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text))
  }

  actual fun toast(text: String, length: ToastLength) {
    Toast.makeText(context, text, length.toAndroidToastLength()).show()
  }

  @SuppressLint("NewApi")
  actual fun downloadByteArray(byteArray: ByteArray, fileName: String, contentType: String) {
    assert(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { "Android 10+ only" }
    var newFileName = fileName
    var counter = 1
    val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
    val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
    val selectionArgs = arrayOf(newFileName.substringBeforeLast('.'))

    val cursor = context.contentResolver.query(
      MediaStore.Downloads.EXTERNAL_CONTENT_URI,
      projection,
      selection,
      selectionArgs,
      null,
    )

    if (cursor != null && cursor.count > 0) {
      while (cursor.moveToNext()) {
        val existingFileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
        if (existingFileName.startsWith(fileName.substringBeforeLast("."))) {
          newFileName = context.getString(
            MR.strings.photo_filename_format.resourceId,
            fileName.substringBeforeLast("."),
            counter,
            fileName.substringAfterLast("."),
          )
          counter++
          cursor.moveToPosition(-1)
        }
      }
      cursor.close()
    }

    val contentValues = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
      put(MediaStore.MediaColumns.MIME_TYPE, contentType)
      put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
      context.contentResolver.openOutputStream(it).use { outputStream ->
        outputStream?.write(byteArray)
      }
    }
  }

  actual fun shareByteArray(byteArray: ByteArray, fileName: String, contentType: String) {
    val file = File(context.cacheDir, fileName)
    if (file.exists()) file.delete()
    file.createNewFile()
    file.writeBytes(byteArray)
    val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.clipData = ClipData.newRawUri(null, uri)
    intent.type = contentType
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(intent, MR.strings.generic_share.getString(context)))
  }
}

fun ToastLength.toAndroidToastLength(): Int = if (this == ToastLength.Short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
