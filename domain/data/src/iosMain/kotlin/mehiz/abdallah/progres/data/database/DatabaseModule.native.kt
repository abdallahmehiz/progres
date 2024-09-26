package mehiz.abdallah.progres.data.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import mehiz.abdallah.progres.data.db.ProgresDB
import org.koin.dsl.module

actual val ProgresDatabaseModule = module {
  single {
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
