package ui.home.enrollments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.StudentCardModel

class EnrollmentsScreenViewModel(
  private val accountUseCase: AccountUseCase
) : ViewModel() {

  // afaik, there is no specific endpoint to get enrollments from
  private val _enrollments = MutableStateFlow<List<StudentCardModel>>(emptyList())
  val enrollments = _enrollments.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _enrollments.update { accountUseCase.getStudentCards() }
    }
  }
}
