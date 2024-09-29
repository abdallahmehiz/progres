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
import presentation.utils.RequestState

data class HomeScreenUIData(
  val studentCard: StudentCardModel,
  val bacInfo: BacInfoModel,
)

class HomeScreenViewModel(
  private val accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _data = MutableStateFlow<RequestState<HomeScreenUIData>>(RequestState.Loading)
  val data = _data.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _data.update {
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }

  fun refresh() {
    _isRefreshing.update { true }
    viewModelScope.launch(Dispatchers.IO) {
      runCatching { _data.update { RequestState.Success(getData(true)) } }
      _isRefreshing.update { false }
    }
  }

  private suspend fun getData(refresh: Boolean): HomeScreenUIData {
    return HomeScreenUIData(accountUseCase.getLatestStudentCard(refresh), accountUseCase.getBacInfoWithGrades(refresh))
  }
}
