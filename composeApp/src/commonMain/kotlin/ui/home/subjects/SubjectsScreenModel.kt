package ui.home.subjects

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
import mehiz.abdallah.progres.domain.SubjectUseCase
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.SubjectModel
import presentation.utils.RequestState

class SubjectsScreenModel(
  private val subjectUseCase: SubjectUseCase,
) : ScreenModel {

  private val _subjects =
    MutableStateFlow<RequestState<ImmutableMap<AcademicPeriodModel, List<SubjectModel>>>>(RequestState.Loading)
  val subjects = _subjects.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _subjects.update {
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
    }
  }

  suspend fun refresh() {
    _subjects.update { RequestState.Success(getData(true)) }
  }

  private suspend fun getData(refresh: Boolean): ImmutableMap<AcademicPeriodModel, List<SubjectModel>> {
    return subjectUseCase.getAllSubjects(refresh, refresh)
      .sortedBy { it.id }
      .groupBy { it.period }
      .toImmutableMap()
  }
}
