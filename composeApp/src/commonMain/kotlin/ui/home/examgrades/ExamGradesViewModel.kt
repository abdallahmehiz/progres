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
  accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _examGrades =
    MutableStateFlow<RequestState<ImmutableMap<Long, List<ExamGradeModel>>>>(RequestState.Loading)
  val examGrades = _examGrades.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _examGrades.update { _ ->
        try {
          RequestState.Success(
            accountUseCase.getExamGrades()
              .sortedBy { it.periodId }
              .groupBy { it.periodId }
              .toImmutableMap()
          )
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
