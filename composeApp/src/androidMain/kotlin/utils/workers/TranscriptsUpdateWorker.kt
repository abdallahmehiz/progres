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
import mehiz.abdallah.progres.domain.TranscriptUseCase
import mehiz.abdallah.progres.domain.models.TranscriptModel
import mehiz.abdallah.progres.i18n.MR
import utils.FirebaseUtils
import kotlin.math.absoluteValue

class TranscriptsUpdateWorker(
  appContext: Context,
  workerParams: WorkerParameters,
  private val transcriptUseCase: TranscriptUseCase,
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val CHANNEL_ID = "transcripts_updates_channel"
    const val NOTIFICATION_ID = 1002
    const val TRANSCRIPTS_COUNT_KEY = "transcriptsCount"
  }

  override suspend fun doWork(): Result {
    Log.d(TAG, "Transcripts update check started")

    return try {
      val oldTranscripts = transcriptUseCase.getAllTranscripts(false, false)
      val newTranscripts = transcriptUseCase.getAllTranscripts(true, false)

      val newTranscriptsCount = countNewTranscripts(oldTranscripts, newTranscripts)

      if (newTranscriptsCount > 0) {
        sendGradesUpdateNotification(newTranscriptsCount)
        Log.d(TAG, "Found $newTranscriptsCount updated transcripts")
      } else {
        Log.d(TAG, "No changes in transcripts")
      }

      Result.success(
        Data.Builder().putInt(TRANSCRIPTS_COUNT_KEY, newTranscriptsCount).build()
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error checking transcript updates: ${e.message}")
      FirebaseUtils.reportException(e)
      Result.failure(Data.Builder().putString("error", e.message).build())
    }
  }

  private fun countNewTranscripts(
    oldTranscripts: List<TranscriptModel>,
    newTranscripts: List<TranscriptModel>
  ): Int {
    return (oldTranscripts.toSet().size - newTranscripts.toSet().size).absoluteValue
  }

  private fun sendGradesUpdateNotification(count: Int) {
    val notificationManager = applicationContext.getSystemService<NotificationManager>()
    createNotificationChannel(notificationManager)

    val messageText = applicationContext.resources.getQuantityString(
      MR.plurals.new_transcripts_notification_message.resourceId,
      count,
      count
    )

    val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
      .setSmallIcon(R.drawable.notifications_icon)
      .setContentTitle(applicationContext.getString(MR.strings.new_transcripts_notification_title.resourceId))
      .setContentText(messageText)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)

    notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
  }

  private fun createNotificationChannel(notificationManager: NotificationManager?) {
    if (notificationManager == null) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = applicationContext.getString(MR.strings.transcripts_update_channel_name.resourceId)
      val description = applicationContext.getString(MR.strings.transcripts_update_channel_description.resourceId)
      val importance = NotificationManager.IMPORTANCE_HIGH

      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        this.description = description
      }

      notificationManager.createNotificationChannel(channel)
    }
  }
}
