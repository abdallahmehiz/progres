package ui.home.groups

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
import mehiz.abdallah.progres.domain.models.GroupModel
import presentation.utils.RequestState

class GroupsViewModel(
  accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _groups =
    MutableStateFlow<RequestState<ImmutableList<GroupModel>>>(RequestState.Loading)
  val groups = _groups.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _groups.update {
        try {
          RequestState.Success(
            accountUseCase.getAllGroups().sortedByDescending {
              it.assignmentDate.date.toEpochDays()
            }.toImmutableList(),
          )
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
