package utils

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

actual object FirebaseUtils {
  actual fun reportException(throwable: Throwable) {
    Firebase.crashlytics.recordException(throwable)
    Firebase.crashlytics.sendUnsentReports()
  }
}
