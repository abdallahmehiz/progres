package ui.home.enrollments

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
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.StudentCardModel
import presentation.utils.RequestState

class EnrollmentsScreenModel(
  private val accountUseCase: AccountUseCase
) : ScreenModel {

  private val _enrollments =
    MutableStateFlow<RequestState<ImmutableList<StudentCardModel>>>(RequestState.Loading)
  val enrollments = _enrollments.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _enrollments.update {
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
    screenModelScope.launch(Dispatchers.IO) {
      runCatching { _enrollments.update { RequestState.Success(getData(true)) } }
      _isRefreshing.update { false }
    }
  }

  private suspend fun getData(refresh: Boolean): ImmutableList<StudentCardModel> {
    return accountUseCase
      .getStudentCards(refresh)
      .sortedByDescending { it.id }
      .toImmutableList()
  }
}
