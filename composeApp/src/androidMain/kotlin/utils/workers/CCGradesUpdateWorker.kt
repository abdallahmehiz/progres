package utils.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import mehiz.abdallah.progres.R
import mehiz.abdallah.progres.core.TAG
import mehiz.abdallah.progres.domain.CCGradeUseCase
import mehiz.abdallah.progres.domain.models.CCGradeModel
import mehiz.abdallah.progres.i18n.MR
import utils.FirebaseUtils

class CCGradesUpdateWorker(
  appContext: Context,
  workerParams: WorkerParameters,
  private val ccGradeUseCase: CCGradeUseCase,
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val CHANNEL_ID = "cc_grades_updates_channel"
    const val NOTIFICATION_ID = 1003
    const val CC_GRADES_COUNT_KEY = "ccGradesCount"
  }

  override suspend fun doWork(): Result {
    Log.d(TAG, "CC grades update check started")

    return try {
      val storedGrades = ccGradeUseCase.getAllCCGrades(false)
      val newGrades = ccGradeUseCase.getAllCCGrades(true)
      val changedGrades = compareCCGrades(storedGrades, newGrades)

      if (changedGrades.isNotEmpty()) {
        sendGradesUpdateNotification(changedGrades.size)
        Log.d(TAG, "Found ${changedGrades.size} updated CC grades")
        Result.success(
          Data.Builder().putInt(CC_GRADES_COUNT_KEY, changedGrades.size).build()
        )
      } else {
        Log.d(TAG, "No changes in CC grades")
        Result.success()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error checking CC grades updates: ${e.message}")
      FirebaseUtils.reportException(e)
      Result.failure(Data.Builder().putString("error", e.message).build())
    }
  }

  private fun compareCCGrades(
    oldGrades: List<CCGradeModel>,
    newGrades: List<CCGradeModel>
  ): List<CCGradeModel> {
    return newGrades.filter { newGrade ->
      val oldGrade = oldGrades.find { it.id == newGrade.id }
      oldGrade == null || oldGrade.grade != newGrade.grade
    }
  }

  private fun sendGradesUpdateNotification(count: Int) {
    val notificationManager = applicationContext.getSystemService<NotificationManager>()
    createNotificationChannel(notificationManager)

    val messageText = applicationContext.resources.getQuantityString(
      MR.plurals.new_ccgrades_notification_message.resourceId,
      count,
      count
    )

    val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
      .setSmallIcon(R.drawable.notifications_icon)
      .setContentTitle(applicationContext.getString(MR.strings.new_ccgrades_notification_title.resourceId))
      .setContentText(messageText)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)

    notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
  }

  private fun createNotificationChannel(notificationManager: NotificationManager?) {
    if (notificationManager == null) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = applicationContext.getString(MR.strings.ccgrades_update_channel_name.resourceId)
      val description = applicationContext.getString(MR.strings.ccgrades_update_channel_description.resourceId)
      val importance = NotificationManager.IMPORTANCE_HIGH

      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        this.description = description
      }

      notificationManager.createNotificationChannel(channel)
    }
  }
}
