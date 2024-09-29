package ui.home.subjectsschedule

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
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import presentation.utils.RequestState

class SubjectsScheduleScreenViewModel(
  private val accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _schedule =
    MutableStateFlow<RequestState<ImmutableMap<AcademicPeriodModel, List<SubjectScheduleModel>>>>(RequestState.Loading)
  val schedule = _schedule.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _schedule.update { _ ->
        try {
          RequestState.Success(
            accountUseCase.getAllSubjectsSchedule()
              .groupBy { it.period }
              .apply { keys.sortedBy { it.id } }
              .toImmutableMap(),
          )
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
