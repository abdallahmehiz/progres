package ui.home.examgrades

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
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import presentation.utils.RequestState

class ExamGradesViewModel(
  private val accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _examGrades =
    MutableStateFlow<RequestState<ImmutableMap<Long, List<ExamGradeModel>>>>(RequestState.Loading)
  val examGrades = _examGrades.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _examGrades.update { _ ->
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
      runCatching { _examGrades.update { RequestState.Success(getData(true)) } }
      _isRefreshing.update { false }
    }
  }

  private suspend fun getData(refresh: Boolean): ImmutableMap<Long, List<ExamGradeModel>> {
    return accountUseCase.getExamGrades(refresh)
      .sortedBy { it.id }
      .groupBy { it.periodId }
      .toImmutableMap()
  }
}
