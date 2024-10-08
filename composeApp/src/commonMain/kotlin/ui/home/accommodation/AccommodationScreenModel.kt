package ui.home.accommodation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccommodationUseCase
import mehiz.abdallah.progres.domain.models.AccommodationModel
import presentation.utils.RequestState

class AccommodationScreenModel(
  private val accommodationUseCase: AccommodationUseCase,
) : ScreenModel {
  private val _accommodations =
    MutableStateFlow<RequestState<ImmutableList<AccommodationModel>>>(RequestState.Loading)
  val accommodations = _accommodations.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _accommodations.update {
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
    }
  }

  suspend fun refresh() {
    _accommodations.update { RequestState.Success(getData(true)) }
  }

  private suspend fun getData(refresh: Boolean): ImmutableList<AccommodationModel> {
    return accommodationUseCase.getAccommodationStates(refresh).toImmutableList()
  }
}
