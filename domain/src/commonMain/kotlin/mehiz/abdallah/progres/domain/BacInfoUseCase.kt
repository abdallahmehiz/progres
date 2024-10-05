package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.BacGradeDao
import mehiz.abdallah.progres.data.daos.BacInfoDao
import mehiz.abdallah.progres.data.db.BacGradeTable
import mehiz.abdallah.progres.data.db.BacInfoTable
import mehiz.abdallah.progres.domain.models.BacInfoModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import kotlin.uuid.ExperimentalUuidApi

class BacInfoUseCase(
  private val api: ProgresApi,
  private val bacInfoDao: BacInfoDao,
  private val bacGradeDao: BacGradeDao,
  private val userAuthUseCase: UserAuthUseCase
) {

  @OptIn(ExperimentalUuidApi::class)
  private suspend fun getBacInfo(refresh: Boolean): BacInfoTable {
    bacInfoDao.get()?.let {
      if (!refresh) return it
    }
    return api.getBacInfo(uuid = userAuthUseCase.getUuid(), userAuthUseCase.getToken()).toTable().also {
      if (refresh) bacInfoDao.delete()
      bacInfoDao.insert(it)
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  private suspend fun getBacGrades(refresh: Boolean): List<BacGradeTable> {
    bacGradeDao.getAllBacGrades().let {
      if (it.isNotEmpty() && !refresh) return it
    }
    return api.getBacNotes(userAuthUseCase.getUuid(), userAuthUseCase.getToken()).mapNotNull { it.toTable() }.also {
      if (refresh) bacGradeDao.deleteAllBacGrades()
      it.forEach(bacGradeDao::insert)
    }
  }

  suspend fun getBacInfoWithGrades(refresh: Boolean): BacInfoModel {
    return getBacInfo(refresh).toModel(getBacGrades(refresh).map { it.toModel() })
  }
}
