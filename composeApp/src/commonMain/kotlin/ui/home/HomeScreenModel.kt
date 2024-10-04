package ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.AccommodationStateModel
import mehiz.abdallah.progres.domain.models.BacInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import presentation.utils.RequestState

data class HomeScreenUIData(
  val studentCard: StudentCardModel,
  val accommodationStateModel: AccommodationStateModel?,
  val bacInfo: BacInfoModel,
)

class HomeScreenModel(
  private val accountUseCase: AccountUseCase,
) : ScreenModel {

  private val _data = MutableStateFlow<RequestState<HomeScreenUIData>>(RequestState.Loading)
  val data = _data.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _data.update {
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
      _data.value.getSuccessDataOrNull()?.let {
        runCatching {
          _data.update { _ ->
            getAccommodationState(it.studentCard.id)?.let { state ->
              RequestState.Success(it.copy(accommodationStateModel = state))
            } ?: return@let
          }
        }
      }
    }
  }

  suspend fun refresh() {
    _data.update { RequestState.Success(getData(true)) }
  }

  private suspend fun getAccommodationState(cardId: Long): AccommodationStateModel? {
    return accountUseCase.getAccommodationStateForCard(cardId)
  }

  private suspend fun getData(refresh: Boolean): HomeScreenUIData {
    return HomeScreenUIData(
      accountUseCase.getLatestStudentCard(refresh),
      null,
      accountUseCase.getBacInfoWithGrades(refresh),
    )
  }
}
