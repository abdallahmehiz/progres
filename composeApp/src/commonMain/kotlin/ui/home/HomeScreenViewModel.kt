package ui.home

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
import mehiz.abdallah.progres.domain.models.StudentCardModel

class HomeScreenViewModel(
  private val accountUseCase: AccountUseCase
) : ViewModel() {

  private val _studentCard = MutableStateFlow<StudentCardModel?>(null)
  val studentCard = _studentCard.asStateFlow()

  private val _bacInfo = MutableStateFlow<BacInfoModel?>(null)
  val bacInfo = _bacInfo.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _studentCard.update { accountUseCase.getLatestStudentCard() }
      _bacInfo.update { accountUseCase.getBacInfoWithGrades() }
    }
  }
}
