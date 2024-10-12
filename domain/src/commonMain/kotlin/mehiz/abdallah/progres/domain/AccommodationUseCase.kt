package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.AccommodationDao
import mehiz.abdallah.progres.domain.models.AccommodationModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import kotlin.uuid.ExperimentalUuidApi

class AccommodationUseCase(
  private val api: ProgresApi,
  private val accommodationDao: AccommodationDao,
  private val userAuthUseCase: UserAuthUseCase,
) {

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getAccommodationStates(
    refresh: Boolean,
  ): List<AccommodationModel> {
    accommodationDao.getAllAccommodationsState().let {
      if (it.isEmpty() || refresh) return@let
      return it.map { it.toModel() }
    }
    val uuid = userAuthUseCase.getUuid()
    val token = userAuthUseCase.getToken()
    val states = api.getAccommodationStates(uuid, token).map { it.toTable() }
    if (refresh) accommodationDao.deleteAllAccommodationStates()
    return states.map {
      accommodationDao.insert(it)
      it.toModel()
    }
  }

  suspend fun getAccommodationStateForCard(id: Long): AccommodationModel? {
    return getAccommodationStates(false).firstOrNull { it.cardId == id }
  }
}
