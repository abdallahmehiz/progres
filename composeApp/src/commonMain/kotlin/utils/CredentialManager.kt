package utils

expect class CredentialManager {
  suspend fun signUp(username: String, password: String)
  suspend fun signIn(): Pair<String, String>?
}
