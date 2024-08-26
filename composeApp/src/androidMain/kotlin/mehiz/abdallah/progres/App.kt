package mehiz.abdallah.progres

import android.app.Application
import de.halfbit.logger.initializeLogger
import de.halfbit.logger.sink.println.registerPrintlnSink

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    initializeLogger {
      registerPrintlnSink()
    }
  }
}
