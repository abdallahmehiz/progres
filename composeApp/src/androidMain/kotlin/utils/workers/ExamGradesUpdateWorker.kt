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
import mehiz.abdallah.progres.domain.ExamGradeUseCase
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import mehiz.abdallah.progres.i18n.MR
import utils.FirebaseUtils

class ExamGradesUpdateWorker(
  appContext: Context,
  workerParams: WorkerParameters,
  private val examGradesUseCase: ExamGradeUseCase,
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    private const val CHANNEL_ID = "exam_grades_updates"
    private const val NOTIFICATION_ID = 1001
  }

  override suspend fun doWork(): Result {
    Log.d(TAG, "Exam grades update check started")

    return try {
      val storedGrades = examGradesUseCase.getExamGrades(false, propagateRefresh = false)
      val newGrades = examGradesUseCase.getExamGrades(true, propagateRefresh = false)
      val changedGrades = compareExamGrades(storedGrades, newGrades)

      if (changedGrades.isNotEmpty()) {
        sendGradesUpdateNotification(changedGrades.size)
        Log.d(TAG, "Found ${changedGrades.size} updated exam grades")
        Result.success()
      } else {
        Log.d(TAG, "No changes in exam grades")
        Result.success()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error checking exam grades updates: ${e.message}")
      FirebaseUtils.reportException(e)
      Result.failure(Data.Builder().putString("error", e.message).build())
    }
  }

  private fun compareExamGrades(
    oldGrades: List<ExamGradeModel>,
    newGrades: List<ExamGradeModel>
  ): List<ExamGradeModel> {
    return newGrades.filter { newGrade ->
      val oldGrade = oldGrades.find { it.id == newGrade.id }
      oldGrade == null || oldGrade.grade != newGrade.grade
    }
  }

  private fun sendGradesUpdateNotification(count: Int) {
    val notificationManager = applicationContext.getSystemService<NotificationManager>()
    createNotificationChannel(notificationManager)

    val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
      .setSmallIcon(R.drawable.notifications_icon).setContentTitle(
        MR.strings.new_exams_grades_notification_title.getString(
          applicationContext
        )
      ).setContentText(
        applicationContext.resources.getQuantityString(
          MR.plurals.new_examgrades_notification_message.resourceId,
          count,
          count
        )
      ).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true)

    notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
  }

  private fun createNotificationChannel(notificationManager: NotificationManager?) {
    if (notificationManager == null) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name =
        applicationContext.getString(MR.strings.exams_grades_update_channel_name.resourceId)
      val description =
        applicationContext.getString(MR.strings.exams_grades_update_channel_description.resourceId)
      val importance = NotificationManager.IMPORTANCE_HIGH

      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        this.description = description
      }

      notificationManager.createNotificationChannel(channel)
    }
  }
}
