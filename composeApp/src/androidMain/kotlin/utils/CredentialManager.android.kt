package utils

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential

actual class CredentialManager(private val context: Context) {

  private val manager = CredentialManager.create(this.context)

  actual suspend fun signUp(username: String, password: String) {
    runCatching {
      manager.createCredential(
        context,
        CreatePasswordRequest(
          username,
          password,
        ),
      )
    }
  }

  actual suspend fun signIn(): Pair<String, String>? {
    return runCatching {
      (
        manager.getCredential(
          context,
          GetCredentialRequest(listOf(GetPasswordOption())),
        ).credential as PasswordCredential
        ).let {
        Pair(it.id, it.password)
      }
    }.getOrNull()
  }
}
