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
          
          override fun onUpgrade(
            db: SupportSQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
          ) {
            // Handle migration from BLOB to file storage
            // Clear affected tables to force fresh sync with new schema
            if (oldVersion < newVersion) {
              try {
                db.execSQL("DROP TABLE IF EXISTS IndividualInfoTable")
                db.execSQL("DROP TABLE IF EXISTS StudentCardTable")
                // Recreate tables with new schema
                onCreate(db)
              } catch (e: Exception) {
                // If migration fails, recreate database
                super.onUpgrade(db, oldVersion, newVersion)
              }
            }
          }
        }
      ),
    )
  }
}
