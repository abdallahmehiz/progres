package ui.home.bacinfoscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.BacInfoModel

class BacInfoScreenViewModel(
  accountUseCase: AccountUseCase
) : ViewModel() {

  private val _bacInfo = MutableStateFlow<BacInfoModel?>(null)
  val bacInfo = _bacInfo.asStateFlow()

  private val _studentPhoto = MutableStateFlow<ByteArray?>(null)
  val studentPhoto = _studentPhoto.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _bacInfo.update { accountUseCase.getBacInfoWithGrades() }
      _studentPhoto.update { accountUseCase.getStudentPhoto() }
    }
  }
}
