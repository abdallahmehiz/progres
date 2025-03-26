package utils.workers

import AppUpdateCheck
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import mehiz.abdallah.progres.R
import mehiz.abdallah.progres.core.TAG
import mehiz.abdallah.progres.i18n.MR
import utils.FirebaseUtils

class AppUpdateCheckWorker(
  appContext: Context,
  workerParams: WorkerParameters,
  private val appUpdateCheck: AppUpdateCheck,
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val CHANNEL_ID = "app_update_channel"
    const val NOTIFICATION_ID = 1005
  }

  override suspend fun doWork(): Result {
    Log.d(TAG, "App update check started")

    return try {
      delay(2000) // wait for the update check
      if (appUpdateCheck.isThereAnUpdate.value) {
        Log.d(TAG, "New app update available")
        sendUpdateNotification()
      } else {
        Log.d(TAG, "App is up to date")
      }

      Result.success()
    } catch (e: Exception) {
      Log.e(TAG, "Error checking for app updates: ${e.message}")
      FirebaseUtils.reportException(e)
      Result.failure()
    }
  }

  private fun sendUpdateNotification() {
    val notificationManager = applicationContext.getSystemService<NotificationManager>()
    createNotificationChannel(notificationManager)

    val githubIntent = Intent(Intent.ACTION_VIEW).apply {
      data = (MR.strings.repository_url.getString(applicationContext) + "/releases/latest").toUri()
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
      applicationContext,
      0,
      githubIntent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
      .setSmallIcon(R.drawable.notifications_icon)
      .setContentTitle(applicationContext.getString(MR.strings.new_update_available.resourceId))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
  }

  private fun createNotificationChannel(notificationManager: NotificationManager?) {
    if (notificationManager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val name = "App Updates"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, name, importance)

    notificationManager.createNotificationChannel(channel)
  }
}
