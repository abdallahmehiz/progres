package ui.home.enrollments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.StudentCardModel
import presentation.utils.RequestState

class EnrollmentsScreenViewModel(
  private val accountUseCase: AccountUseCase
) : ViewModel() {

  private val _enrollments =
    MutableStateFlow<RequestState<ImmutableList<StudentCardModel>>>(RequestState.Loading)
  val enrollments = _enrollments.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _enrollments.update {
        try {
          RequestState.Success(accountUseCase.getStudentCards().toImmutableList())
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
