package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import kotlin.uuid.ExperimentalUuidApi

class StudentCardUseCase(
  private val api: ProgresApi,
  private val studentCardDao: StudentCardDao,
  private val individualInfoDao: IndividualInfoDao,
  private val userAuthUseCase: UserAuthUseCase,
) {
  suspend fun getLatestStudentPhoto(refresh: Boolean): ByteArray? {
    return getLatestStudentCard(refresh).photo
  }

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getAllStudentCards(refresh: Boolean): List<StudentCardModel> {
    studentCardDao.getAllStudentCards().let {
      val individual = getIndividualInfo(false)
      if (it.isNotEmpty() && !refresh) {
        return it.map { it.toModel(individual.photo) }
      }
    }
    val token = userAuthUseCase.getToken()
    val auth = userAuthUseCase.getFullUserAuth()
    val individual = getIndividualInfo(refresh)
    val establishmentLogo = api.getEstablishmentLogo(auth.establishmentId, token)
    val cards = api.getStudentCards(auth.uuid, token).map { card ->
      card.toTable(api.getTransportState(auth.uuid, card.id, token)?.transportPayed == true, establishmentLogo)
    }
    if (refresh) studentCardDao.deleteAllCards()
    return cards.map {
      studentCardDao.insert(it)
      it.toModel(
        photo = individual.photo,
      )
    }
  }

  suspend fun getLatestStudentCard(refresh: Boolean): StudentCardModel {
    return if (refresh) {
      getAllStudentCards(true).maxBy { it.id }
    } else {
      studentCardDao.getLatestStudentCard()?.let {
        val individualInfo = getIndividualInfo(false)
        it.toModel(individualInfo.photo)
      } ?: getAllStudentCards(false).maxBy { it.id }
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getIndividualInfo(refresh: Boolean): IndividualInfoModel {
    individualInfoDao.getIndividualInfo()?.let {
      if (!refresh) return it.toModel()
    }
    val uuid = userAuthUseCase.getUuid()
    val token = userAuthUseCase.getToken()
    val info = api.getIndividualInfo(uuid, token)
    val photo = api.getStudentPhoto(uuid = uuid, token = token)
    if (refresh) individualInfoDao.deleteAllIndividualInfo()
    return info.let { dto ->
      dto.toTable(
        photo,
        uuid = uuid.toString(),
      ).also { individualInfoDao.insert(it) }
    }.toModel()
  }
}
