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
  accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _ccGrades =
    MutableStateFlow<RequestState<ImmutableMap<AcademicPeriodModel, List<CCGradeModel>>>>(RequestState.Loading)
  val ccGrades = _ccGrades.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _ccGrades.update {
        try {
          RequestState.Success(
            accountUseCase.getAllCCGrades()
              .sortedBy { it.id }
              .groupBy { it.period }.toImmutableMap(),
          )
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
