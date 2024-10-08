package ui.home.discharge

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.DischargeUseCase
import mehiz.abdallah.progres.domain.models.DischargeModel
import presentation.utils.RequestState

class DischargeScreenModel(
  private val dischargeUseCase: DischargeUseCase,
) : ScreenModel {

  private val _discharge = MutableStateFlow<RequestState<DischargeModel?>>(RequestState.Loading)
  val discharge = _discharge.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _discharge.update { _ ->
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
    }
  }

  suspend fun refresh() {
    _discharge.update { RequestState.Success(getData(true)) }
  }

  suspend fun getData(refresh: Boolean): DischargeModel? {
    return dischargeUseCase.getDischargeState(refresh)
  }
}
