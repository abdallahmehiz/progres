package mehiz.abdallah.progres.data.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import mehiz.abdallah.progres.data.db.ProgresDB
import org.kodein.di.DI
import org.kodein.di.bindSingleton

actual val ProgresDatabaseModule: DI.Module = DI.Module("ProgresDatabaseModule") {
  bindSingleton {
    ProgresDB(
      NativeSqliteDriver(
        ProgresDB.Schema,
        "progres.db",
        onConfiguration = {
          it.copy(extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true))
        }
      ),
    )
  }
}
