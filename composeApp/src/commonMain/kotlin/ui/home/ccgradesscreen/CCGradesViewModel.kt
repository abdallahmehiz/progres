package ui.home.ccgradesscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.CCGradeModel

class CCGradesViewModel(
  accountUseCase: AccountUseCase
) : ViewModel() {

  private val _ccGrades =
    MutableStateFlow<Map<AcademicPeriodModel, List<CCGradeModel>>>(emptyMap())
  val ccGrades = _ccGrades.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _ccGrades.update {
        accountUseCase.getAllCCGrades()
          .sortedBy { it.id }
          .groupBy { it.period }
      }
    }
  }
}
