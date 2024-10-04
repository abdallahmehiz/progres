package ui.home.examsschedule

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
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.ExamScheduleModel
import presentation.utils.RequestState

class ExamsScheduleScreenModel(
  private val accountUseCase: AccountUseCase,
) : ScreenModel {

  private val _examSchedules =
    MutableStateFlow<RequestState<ImmutableMap<AcademicPeriodModel, List<ExamScheduleModel>>>>(RequestState.Loading)
  val examSchedules = _examSchedules.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _examSchedules.update {
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
    }
  }

  suspend fun refresh() {
    _examSchedules.update { RequestState.Success(getData(true)) }
  }

  private suspend fun getData(refresh: Boolean): ImmutableMap<AcademicPeriodModel, List<ExamScheduleModel>> {
    return accountUseCase.getExamSchedules(refresh, refresh)
      .sortedBy { it.id }
      .groupBy { it.period }
      .toImmutableMap()
  }
}
