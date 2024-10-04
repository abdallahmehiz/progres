package ui.home.bacinfoscreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.BacInfoModel

class BacInfoScreenModel(
  accountUseCase: AccountUseCase
) : ScreenModel {

  private val _bacInfo = MutableStateFlow<BacInfoModel?>(null)
  val bacInfo = _bacInfo.asStateFlow()

  private val _studentPhoto = MutableStateFlow<ByteArray?>(null)
  val studentPhoto = _studentPhoto.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _bacInfo.update { accountUseCase.getBacInfoWithGrades(false) }
      _studentPhoto.update { accountUseCase.getStudentPhoto(false) }
    }
  }
}
