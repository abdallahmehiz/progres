package ui.home.ccgradesscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.CCGradeModel
import presentation.utils.RequestState

class CCGradesViewModel(
  private val accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _ccGrades =
    MutableStateFlow<RequestState<ImmutableMap<AcademicPeriodModel, List<CCGradeModel>>>>(RequestState.Loading)
  val ccGrades = _ccGrades.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _ccGrades.update {
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
      runCatching { _ccGrades.update { RequestState.Success(getData(true)) } }
      _isRefreshing.update { false }
    }
  }

  private suspend fun getData(refresh: Boolean): ImmutableMap<AcademicPeriodModel, List<CCGradeModel>> {
    return accountUseCase.getAllCCGrades(refresh)
      .sortedBy { it.id }
      .groupBy { it.period }
      .toImmutableMap()
  }
}
