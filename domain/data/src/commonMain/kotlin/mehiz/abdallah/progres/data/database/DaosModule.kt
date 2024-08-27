package mehiz.abdallah.progres.data.database

import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.data.daos.UserAuthDao
import org.kodein.di.DI
import org.kodein.di.bindSingletonOf

val DaosModule = DI.Module("DaosModule") {
  bindSingletonOf(::UserAuthDao)
  bindSingletonOf(::StudentCardDao)
  bindSingletonOf(::IndividualInfoDao)
}
