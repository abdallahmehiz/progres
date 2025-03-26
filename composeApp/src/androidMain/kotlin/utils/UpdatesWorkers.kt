package utils

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import mehiz.abdallah.progres.core.TAG
import utils.workers.AppUpdateCheckWorker
import utils.workers.CCGradesUpdateWorker
import utils.workers.ExamGradesUpdateWorker
import utils.workers.TranscriptsUpdateWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

object UpdatesWorkers {
  private const val EXAM_GRADES_UPDATE_WORK = "exam_grades_update_work"
  private const val EXAM_GRADES_IMMEDIATE_WORK = "exam_grades_immediate_work"
  private const val TRANSCRIPTS_UPDATE_WORK = "transcripts_update_work"
  private const val TRANSCRIPTS_IMMEDIATE_WORK = "transcripts_immediate_work"
  private const val CC_GRADES_UPDATE_WORK = "cc_grades_update_work"
  private const val CC_GRADES_IMMEDIATE_WORK = "cc_grades_immediate_work"

  fun scheduleExamGradesUpdateWork(context: Context) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val examGradesUpdateRequest =
      PeriodicWorkRequestBuilder<ExamGradesUpdateWorker>(Duration.ofHours(1))
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      EXAM_GRADES_UPDATE_WORK,
      ExistingPeriodicWorkPolicy.KEEP,
      examGradesUpdateRequest,
    )

    runExamGradesUpdateWorkImmediately(context)
  }

  fun runExamGradesUpdateWorkImmediately(context: Context) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val examGradesUpdateRequest = OneTimeWorkRequestBuilder<ExamGradesUpdateWorker>()
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
      EXAM_GRADES_IMMEDIATE_WORK,
      ExistingWorkPolicy.REPLACE,
      examGradesUpdateRequest,
    )

    Log.d(TAG, "Scheduled immediate exam grades update for testing")
  }

  fun scheduleTranscriptsUpdateWork(context: Context) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val transcriptsUpdateRequest =
      PeriodicWorkRequestBuilder<TranscriptsUpdateWorker>(Duration.ofHours(1))
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      TRANSCRIPTS_UPDATE_WORK,
      ExistingPeriodicWorkPolicy.KEEP,
      transcriptsUpdateRequest
    )
    runTranscriptsUpdateWorkImmediately(context)
  }

  fun runTranscriptsUpdateWorkImmediately(context: Context) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val transcriptsUpdateRequest = OneTimeWorkRequestBuilder<TranscriptsUpdateWorker>()
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
      TRANSCRIPTS_IMMEDIATE_WORK,
      ExistingWorkPolicy.REPLACE,
      transcriptsUpdateRequest
    )

    Log.d(TAG, "Scheduled immediate transcripts update for testing")
  }

  fun scheduleCCGradesUpdateWork(context: Context) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val ccGradesUpdateRequest =
      PeriodicWorkRequestBuilder<CCGradesUpdateWorker>(Duration.ofHours(1))
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      CC_GRADES_UPDATE_WORK,
      ExistingPeriodicWorkPolicy.KEEP,
      ccGradesUpdateRequest
    )

    runCCGradesUpdateWorkImmediately(context)
  }

  fun runCCGradesUpdateWorkImmediately(context: Context) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val ccGradesUpdateRequest = OneTimeWorkRequestBuilder<CCGradesUpdateWorker>()
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
      CC_GRADES_IMMEDIATE_WORK,
      ExistingWorkPolicy.REPLACE,
      ccGradesUpdateRequest
    )

    Log.d(TAG, "Scheduled immediate CC grades update for testing")
  }

  fun scheduleAppUpdateCheckWork(context: Context) {
    val appUpdateCheckRequest = PeriodicWorkRequestBuilder<AppUpdateCheckWorker>(
      repeatInterval = 24,
      repeatIntervalTimeUnit = TimeUnit.HOURS
    ).build()

    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        "app_update_check",
        ExistingPeriodicWorkPolicy.UPDATE,
        appUpdateCheckRequest
      )
  }

  fun runAppUpdateCheckWorkImmediately(context: Context) {
    val appUpdateCheckRequest = OneTimeWorkRequestBuilder<AppUpdateCheckWorker>().build()

    WorkManager.getInstance(context)
      .enqueueUniqueWork(
        "app_update_check_immediate",
        ExistingWorkPolicy.REPLACE,
        appUpdateCheckRequest
      )
  }
}
