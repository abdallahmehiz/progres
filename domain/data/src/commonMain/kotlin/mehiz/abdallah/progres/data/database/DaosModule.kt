package mehiz.abdallah.progres.data.database

import mehiz.abdallah.progres.data.daos.UserAuthDao
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val DaosModule = DI.Module("DaosModule") {
  bindSingleton { UserAuthDao(instance()) }
}
