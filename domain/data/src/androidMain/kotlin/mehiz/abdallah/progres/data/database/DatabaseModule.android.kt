package mehiz.abdallah.progres.data.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import mehiz.abdallah.progres.data.db.ProgresDB
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual val ProgresDatabaseModule: DI.Module = DI.Module("ProgresDatabaseModule") {
  bindSingleton {
    ProgresDB(
      AndroidSqliteDriver(
        ProgresDB.Schema,
        instance(),
        name = "progres.db",
        callback = object : AndroidSqliteDriver.Callback(ProgresDB.Schema) {
          @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
          override fun onOpen(db: SupportSQLiteDatabase) {
            db.setForeignKeyConstraintsEnabled(true)
          }
        }
      ),
    )
  }
}
