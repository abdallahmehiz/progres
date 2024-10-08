package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.data.daos.EstablishmentDao
import mehiz.abdallah.progres.data.db.EstablishmentTable
import mehiz.abdallah.progres.domain.models.EstablishmentModel
import mehiz.abdallah.progres.domain.models.toModel

class EstablishmentUseCase(private val establishmentDao: EstablishmentDao) {
  fun getEstablishment(id: Long): EstablishmentModel? {
    return establishmentDao.getEstablishmentById(id)?.toModel()
  }

  fun getAllEstablishments(): List<EstablishmentModel> {
    return establishmentDao.getAllEstablishments().map { it.toModel() }
  }

  fun putEstablishment(establishmentTable: EstablishmentTable) {
    establishmentDao.insert(establishmentTable)
  }
}
