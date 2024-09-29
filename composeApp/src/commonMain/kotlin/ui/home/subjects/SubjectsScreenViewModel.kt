package ui.home.subjects

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
import mehiz.abdallah.progres.domain.models.SubjectModel
import presentation.utils.RequestState

class SubjectsScreenViewModel(
  accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _subjects =
    MutableStateFlow<RequestState<ImmutableMap<String, Map<String, List<SubjectModel>>>>>(RequestState.Loading)
  val subjects = _subjects.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _subjects.update {
        try {
          accountUseCase.getAllSubjects().let { subjects ->
            RequestState.Success(
              subjects
                .groupBy { it.levelStringLatin }
                .mapValues { it.value.groupBy { it.periodStringLatin } }
                .toImmutableMap(),
            )
          }
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
