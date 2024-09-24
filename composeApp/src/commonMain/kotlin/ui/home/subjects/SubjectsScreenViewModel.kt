package ui.home.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.SubjectModel

class SubjectsScreenViewModel(
  accountUseCase: AccountUseCase
) : ViewModel() {

  private val _subjects = MutableStateFlow<Map<String, Map<String, List<SubjectModel>>>>(emptyMap())
  val subjects = _subjects.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      accountUseCase.getAllSubjects().let { subjects ->
        _subjects.update {
          subjects.groupBy { it.levelStringLatin }
            .mapValues { it.value.groupBy { it.periodStringLatin } }
        }
      }
    }
  }
}
