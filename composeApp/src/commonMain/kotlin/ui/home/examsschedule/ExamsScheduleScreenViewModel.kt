package ui.home.examsschedule

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
import mehiz.abdallah.progres.domain.models.ExamScheduleModel
import presentation.utils.RequestState

class ExamsScheduleScreenViewModel(
  private val accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _examSchedules =
    MutableStateFlow<RequestState<ImmutableMap<String, List<ExamScheduleModel>>>>(RequestState.Loading)
  val examSchedules = _examSchedules.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _examSchedules.update {
        try {
          RequestState.Success(accountUseCase.getExamSchedules().groupBy { it.periodStringLatin }.toImmutableMap())
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
