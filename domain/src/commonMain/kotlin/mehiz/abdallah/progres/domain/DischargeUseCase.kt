package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.DischargeDao
import mehiz.abdallah.progres.domain.models.DischargeModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import kotlin.uuid.ExperimentalUuidApi

class DischargeUseCase(
  private val api: ProgresApi,
  private val dischargeDao: DischargeDao,
  private val userAuthUseCase: UserAuthUseCase,
) {
  @OptIn(ExperimentalUuidApi::class)
  suspend fun getDischargeState(refresh: Boolean): DischargeModel? {
    dischargeDao.get()?.let {
      if (!refresh) return it.toModel()
    }
    val discharge = api.getDischargeState(userAuthUseCase.getUuid())
    if (refresh) dischargeDao.delete()
    return discharge?.toTable()?.also { dischargeDao.insert(it) }?.toModel()
  }
}
