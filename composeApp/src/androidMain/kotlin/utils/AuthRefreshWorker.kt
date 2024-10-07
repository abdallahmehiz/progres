package utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.kizitonwose.calendar.core.minusDays
import com.liftric.kvault.KVault
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.core.TAG
import mehiz.abdallah.progres.domain.UserAuthUseCase
import preferences.KVaultKeys

class AuthRefreshWorker(
  appContext: Context,
  workerParams: WorkerParameters,
  private val authUseCase: UserAuthUseCase,
  private val kVault: KVault,
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
        val id = kVault.string(KVaultKeys.id)
        checkNotNull(id) { "Vault doesn't contain id" }
        val password = kVault.string(KVaultKeys.password)
        checkNotNull(password) { "Vault doesn't contain password" }
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
