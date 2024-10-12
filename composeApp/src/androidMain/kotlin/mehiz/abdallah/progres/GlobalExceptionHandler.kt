package mehiz.abdallah.progres

import android.content.Context
import android.content.Intent
import utils.FirebaseUtils
import kotlin.system.exitProcess

class GlobalExceptionHandler(
  private val context: Context,
  private val activity: Class<*>
) : Thread.UncaughtExceptionHandler {

  override fun uncaughtException(t: Thread, e: Throwable) {
    FirebaseUtils.reportException(e)
    val intent = Intent(context, activity)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.putExtra("exception", e.stackTraceToString())
    context.startActivity(intent)
    exitProcess(0)
  }
}
