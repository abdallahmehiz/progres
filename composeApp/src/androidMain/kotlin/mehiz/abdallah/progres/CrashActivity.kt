package mehiz.abdallah.progres

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mehiz.abdallah.progres.i18n.MR
import presentation.theme.AppTheme
import ui.crash.CrashScreen
import ui.crash.collectLogs
import java.io.File

class CrashActivity : ComponentActivity() {
  private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        CrashScreen(
          intent.getStringExtra("exception") ?: "",
          logs = collectLogs(),
          onDumpLogs = { logs ->
            lifecycleScope.launch(Dispatchers.IO) {
              dumpLogs(logs)
            }
          },
          onCopyLogs = { clipboardManager.setPrimaryClip(ClipData.newPlainText(null, it)) },
          onAppRestart = {
            finishAndRemoveTask()
            startActivity(Intent(this@CrashActivity, MainActivity::class.java))
          },
        )
      }
    }
  }

  private suspend fun dumpLogs(logs: String) {
    withContext(NonCancellable) {
      val file = File(applicationContext.cacheDir, "progres_logs.txt")
      if (file.exists()) file.delete()
      file.createNewFile()
      file.appendText(logs)
      val uri = FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".provider", file)
      val intent = Intent(Intent.ACTION_SEND)
      intent.putExtra(Intent.EXTRA_STREAM, uri)
      intent.clipData = ClipData.newRawUri(null, uri)
      intent.type = "text/plain"
      intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
      this@CrashActivity.startActivity(
        Intent.createChooser(intent, MR.strings.crash_screen_share.getString(applicationContext)),
      )
    }
  }
}
