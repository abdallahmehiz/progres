package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.AccommodationStateDao
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.domain.models.AccommodationStateModel
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import kotlin.uuid.ExperimentalUuidApi

class StudentCardUseCase(
  private val api: ProgresApi,
  private val studentCardDao: StudentCardDao,
  private val accommodationStateDao: AccommodationStateDao,
  private val individualInfoDao: IndividualInfoDao,
  private val userAuthUseCase: UserAuthUseCase,
) {
  suspend fun getLatestStudentPhoto(refresh: Boolean): ByteArray? {
    return getLatestStudentCard(refresh).photo
  }

  suspend fun getStudentPhotoForCard(id: Long): ByteArray? {
    return studentCardDao.getStudentPhoto(id)
  }

  @OptIn(ExperimentalUuidApi::class)
  private suspend fun getAccommodationStates(refresh: Boolean): List<AccommodationStateModel> {
    accommodationStateDao.getAllAccommodationsState().let {
      if (it.isEmpty() || refresh) return@let
      return it.map { it.toModel() }
    }
    val uuid = userAuthUseCase.getUuid()
    val token = userAuthUseCase.getToken()
    val states = api.getAccommodationStates(uuid, token).map { it.toTable() }.also(::println)
    if (refresh) accommodationStateDao.deleteAllAccommodationStates()
    return states.map {
      accommodationStateDao.insert(it)
      it.toModel()
    }
  }

  suspend fun getAccommodationStateForCard(id: Long): AccommodationStateModel? {
    return getAccommodationStates(false).firstOrNull { it.cardId == id }
  }

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getAllStudentCards(refresh: Boolean): List<StudentCardModel> {
    studentCardDao.getAllStudentCards().let {
      if (it.isNotEmpty() && !refresh) return it.map { it.toModel() }
    }
    val uuid = userAuthUseCase.getUuid()
    val token = userAuthUseCase.getToken()
    val cards = api.getStudentCards(uuid, token).map {
      it.toTable(
        api.getStudentPhoto(uuid, token),
        api.getEstablishmentLogo(it.refEtablissementId, token),
        it.transportPaye ?: api.getTransportState(uuid, it.id, token)?.transportPayed ?: false,
      )
    }
    if (refresh) studentCardDao.deleteAllCards()
    return cards.map {
      studentCardDao.insert(it)
      it.toModel()
    }
  }

  suspend fun getLatestStudentCard(refresh: Boolean): StudentCardModel {
    return if (refresh) {
      getAllStudentCards(true).maxBy { it.id }
    } else {
      studentCardDao.getLatestStudentCard()?.toModel() ?: getAllStudentCards(false).maxBy { it.id }
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getIndividualInfo(refresh: Boolean): IndividualInfoModel {
    individualInfoDao.getIndividualInfo()?.let {
      if (!refresh) return it.toModel()
    }
    val info = api.getIndividualInfo(userAuthUseCase.getUuid(), userAuthUseCase.getToken())
    if (refresh) individualInfoDao.deleteAllIndividualInfo()
    return info.let { dto ->
      dto.toTable(getLatestStudentPhoto(refresh)).also { individualInfoDao.insert(it) }
    }.toModel()
  }
}
