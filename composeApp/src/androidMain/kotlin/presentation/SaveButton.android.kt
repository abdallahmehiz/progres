package presentation

import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterState
import dev.icerock.moko.resources.compose.stringResource
import mehiz.abdallah.progres.BuildConfig
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import java.io.File

@Composable
actual fun SaveAndShareButtons(
  byteArray: ByteArray,
  fileName: String,
  modifier: Modifier,
) {
  val context = LocalContext.current
  val toasterState = koinInject<ToasterState>()
  CompositionLocalProvider(LocalContentColor provides Color.White) {
    Row(modifier) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        BoxButton(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          onClick = {
            val file = File(
              Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
              fileName,
            )
            val contentValues = ContentValues().apply {
              put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
              put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
              put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
              resolver.openOutputStream(it).use { it!!.write(byteArray) }
            }
            toasterState.show(MR.strings.photo_saved_success.getString(context), type = ToastType.Success)
          },
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(Icons.Rounded.SaveAlt, null)
            Text(stringResource(MR.strings.generic_save))
          }
        }
      }
      Box(
        Modifier
          .weight(1f)
          .fillMaxHeight()
          .clickable {
            val file = File(context.cacheDir, fileName)
              .also { it.writeBytes(byteArray) }
            val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.clipData = ClipData.newRawUri(null, uri)
            intent.type = "image/jpg"
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(
              Intent.createChooser(intent, MR.strings.crash_screen_share.getString(context)),
            )
          },
        contentAlignment = Alignment.Center,
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Icon(Icons.Rounded.Share, null)
          Text(stringResource(MR.strings.generic_share))
        }
      }
    }
  }
}