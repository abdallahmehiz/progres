package ui.home.subjectsschedule

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
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel

class SubjectsScheduleScreenViewModel(
  private val accountUseCase: AccountUseCase
) : ViewModel() {

  private val _schedule = MutableStateFlow<Map<AcademicPeriodModel, List<SubjectScheduleModel>>>(emptyMap())
  val schedule = _schedule.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _schedule.update {
        accountUseCase.getAllSubjectsSchedule()
          .groupBy { it.period }
          .apply { it.keys.sortedBy { it.id } }
      }
    }
  }
}
