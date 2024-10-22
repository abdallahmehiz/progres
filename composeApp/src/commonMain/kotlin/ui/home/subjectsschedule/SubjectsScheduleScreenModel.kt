package ui.home.subjectsschedule

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.SubjectScheduleUseCase
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import presentation.utils.RequestState

class SubjectsScheduleScreenModel(
  private val subjectScheduleUseCase: SubjectScheduleUseCase,
) : ScreenModel {

  private val _schedule =
    MutableStateFlow<RequestState<ImmutableMap<AcademicPeriodModel?, List<SubjectScheduleModel>>>>(RequestState.Loading)
  val schedule = _schedule.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _schedule.update { _ ->
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
    }
  }

  suspend fun refresh() {
    _schedule.update { RequestState.Success(getData(true)) }
  }

  private suspend fun getData(refresh: Boolean): ImmutableMap<AcademicPeriodModel?, List<SubjectScheduleModel>> {
    return subjectScheduleUseCase.getAllSubjectsSchedule(refresh, refresh)
      .sortedBy { it.period?.id }
      .groupBy { it.period }
      .toImmutableMap()
  }
}
