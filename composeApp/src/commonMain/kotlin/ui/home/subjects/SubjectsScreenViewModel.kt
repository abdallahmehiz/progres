package ui.home.subjects

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
import mehiz.abdallah.progres.domain.models.SubjectModel
import presentation.utils.RequestState

class SubjectsScreenViewModel(
  private val accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _subjects =
    MutableStateFlow<RequestState<ImmutableMap<String, Map<String, List<SubjectModel>>>>>(RequestState.Loading)
  val subjects = _subjects.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _subjects.update {
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
      runCatching { _subjects.update { RequestState.Success(getData(true)) } }
      _isRefreshing.update { false }
    }
  }

  private suspend fun getData(refresh: Boolean): ImmutableMap<String, Map<String, List<SubjectModel>>> {
    return accountUseCase.getAllSubjects(refresh, true)
      .sortedBy { it.id }
      .groupBy { it.levelStringLatin }
      .mapValues { it.value.groupBy { it.periodStringLatin } }
      .toImmutableMap()
  }
}
