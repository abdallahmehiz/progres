package mehiz.abdallah.progres.data.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import mehiz.abdallah.progres.data.db.ProgresDB
import org.koin.dsl.module

actual val ProgresDatabaseModule = module {
  single {
    ProgresDB(
      AndroidSqliteDriver(
        ProgresDB.Schema,
        get(),
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
