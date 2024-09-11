package ui.home.examsschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.ExamScheduleModel

class ExamsScheduleScreenViewModel(
  private val accountUseCase: AccountUseCase
) : ViewModel() {

  private val _examSchedules = MutableStateFlow<Map<String, List<ExamScheduleModel>>>(emptyMap())
  val examSchedules = _examSchedules.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _examSchedules.update {
        accountUseCase.getExamSchedules().groupBy {
          it.periodStringLatin
        }
      }
    }
  }
}
