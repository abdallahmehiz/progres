package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.data.db.EstablishmentTable
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import kotlin.uuid.ExperimentalUuidApi

class StudentCardUseCase(
  private val api: ProgresApi,
  private val studentCardDao: StudentCardDao,
  private val establishmentUseCase: EstablishmentUseCase,
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
  suspend fun getAllStudentCards(refresh: Boolean): List<StudentCardModel> {
    studentCardDao.getAllStudentCards().let {
      if (it.isNotEmpty() && !refresh) {
        return it.map {
          it.toModel(establishment = establishmentUseCase.getEstablishment(it.establishmentId)!!)
        }
      }
    }
    val uuid = userAuthUseCase.getUuid()
    val token = userAuthUseCase.getToken()
    val individual = getIndividualInfo(refresh)
    val cards = api.getStudentCards(uuid, token).map { card ->
      if (establishmentUseCase.getEstablishment(card.refEtablissementId) == null) {
        establishmentUseCase.putEstablishment(
          EstablishmentTable(
            card.refEtablissementId,
            nameLatin = card.llEtablissementLatin,
            nameArabic = card.llEtablissementArabe,
            code = card.refCodeEtablissement,
            photo = api.getEstablishmentLogo(card.refEtablissementId, token),
          ),
        )
      }

      card.toTable(
        individual.photo,
        card.transportPaye ?: api.getTransportState(uuid, card.id, token)?.transportPayed ?: false,
      )
    }
    if (refresh) studentCardDao.deleteAllCards()
    return cards.map {
      studentCardDao.insert(it)
      it.toModel(establishment = establishmentUseCase.getEstablishment(id = it.establishmentId)!!)
    }
  }

  suspend fun getLatestStudentCard(refresh: Boolean): StudentCardModel {
    return if (refresh) {
      getAllStudentCards(true).maxBy { it.id }
    } else {
      studentCardDao.getLatestStudentCard()?.let {
        it.toModel(establishmentUseCase.getEstablishment(it.establishmentId)!!)
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
