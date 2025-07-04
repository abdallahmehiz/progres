package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.IndividualInfoDao
import mehiz.abdallah.progres.data.daos.StudentCardDao
import mehiz.abdallah.progres.domain.models.IndividualInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import utils.FileStorageManager
import kotlin.uuid.ExperimentalUuidApi

class StudentCardUseCase(
    private val api: ProgresApi,
    private val studentCardDao: StudentCardDao,
    private val individualInfoDao: IndividualInfoDao,
    private val userAuthUseCase: UserAuthUseCase,
    private val fileStorageManager: FileStorageManager,
) {
    suspend fun getLatestStudentPhoto(refresh: Boolean): ByteArray? {
        return getLatestStudentCard(refresh).photo
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getAllStudentCards(refresh: Boolean): List<StudentCardModel> {
        studentCardDao.getAllStudentCards().let { cards ->
            val individual = getIndividualInfo(false)
            if (cards.isNotEmpty() && !refresh) {
                return cards.map { card ->
                    // Load establishment logo from file
                    val logoBytes = card.establishmentLogoPath?.let {
                        fileStorageManager.loadImage(it)
                    }
                    card.toModel(individual.photo).copy(establishmentLogo = logoBytes)
                }
            }
        }
        val token = userAuthUseCase.getToken()
        val auth = userAuthUseCase.getFullUserAuth()
        val individual = getIndividualInfo(refresh)

        // Download and save establishment logo
        val establishmentLogoBytes = api.getEstablishmentLogo(auth.establishmentId, token)
        val establishmentId = auth.establishmentId.toString()
        val logoPath = establishmentLogoBytes?.let {
            fileStorageManager.saveUniversityLogo(establishmentId, it)
        }

        val cards = api.getStudentCards(auth.uuid, token).map { card ->
            card.toTable(api.getTransportState(auth.uuid, card.id, token)?.transportPayed == true, logoPath)
        }

        if (refresh) studentCardDao.deleteAllCards()
        return cards.map { card ->
            studentCardDao.insert(card)
            card.toModel(photo = individual.photo).copy(establishmentLogo = establishmentLogoBytes)
        }
    }

    suspend fun getLatestStudentCard(refresh: Boolean): StudentCardModel {
        return if (refresh) {
            getAllStudentCards(true).maxBy { it.id }
        } else {
            studentCardDao.getLatestStudentCard()?.let { card ->
                val individualInfo = getIndividualInfo(false)
                // Load establishment logo from file
                val logoBytes = card.establishmentLogoPath?.let {
                    fileStorageManager.loadImage(it)
                }
                card.toModel(individualInfo.photo).copy(establishmentLogo = logoBytes)
            } ?: getAllStudentCards(false).maxBy { it.id }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getIndividualInfo(refresh: Boolean): IndividualInfoModel {
        individualInfoDao.getIndividualInfo()?.let { table ->
            if (!refresh) {
                val photo = individualInfoDao.getIndividualPhotoById(table.id)
                return table.toModel().copy(photo = photo)
            }
        }
        val uuid = userAuthUseCase.getUuid()
        val token = userAuthUseCase.getToken()
        val info = api.getIndividualInfo(uuid, token)
        val photoBytes = api.getStudentPhoto(uuid = uuid, token = token)

        if (refresh) individualInfoDao.deleteAllIndividualInfo()

        // Save photo to file and get path
        val photoPath = photoBytes?.let {
            fileStorageManager.saveProfilePicture(uuid.toString(), it)
        }

        return info.let { dto ->
            val table = dto.toTable(
                photoPath = photoPath,
                uuid = uuid.toString(),
            )
            individualInfoDao.insert(table)
            table.toModel().copy(photo = photoBytes)
        }
    }
}
