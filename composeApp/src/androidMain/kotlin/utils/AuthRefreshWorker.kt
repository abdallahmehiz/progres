package utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.kizitonwose.calendar.core.minusDays
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.core.TAG
import mehiz.abdallah.progres.domain.UserAuthUseCase

class AuthRefreshWorker(
  appContext: Context,
  workerParams: WorkerParameters,
  private val authUseCase: UserAuthUseCase,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    Log.d(TAG, "Token refresh worker started")
    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now().toLocalDateTime(timeZone)

    return try {
      val tokenExpiration = authUseCase.getExpirationDate()
      if (tokenExpiration.date.minusDays(14) > now.date) {
        Log.d(
          TAG,
          "Token refresh unneeded: ${tokenExpiration.date.toEpochDays() - now.date.toEpochDays()} days remaining"
        )
        Result.retry()
      } else {
        val id = authUseCase.getUsername()
        val password = authUseCase.getPassword() ?: throw Exception("User credentials unavailable")
        authUseCase.refreshLogin(id, password)
        Log.d(TAG, "Token refreshed successfully")
        Result.success()
      }
    } catch (e: Exception) {
      Log.d(TAG, "Error: ${e.message}")
      Result.failure(Data.Builder().putString("error", e.message).build())
    }
  }
}
