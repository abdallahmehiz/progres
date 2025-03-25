package ui.home.groups

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.GroupUseCase
import mehiz.abdallah.progres.domain.models.GroupModel
import presentation.utils.RequestState

class GroupsScreenModel(
  private val groupUseCase: GroupUseCase,
) : ScreenModel {

  private val _groups =
    MutableStateFlow<RequestState<ImmutableList<GroupModel>>>(RequestState.Loading)
  val groups = _groups.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _groups.update {
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
    }
  }

  suspend fun refresh() {
    _groups.update { RequestState.Success(getData(true)) }
  }

  private suspend fun getData(refresh: Boolean): ImmutableList<GroupModel> {
    return groupUseCase.getAllGroups(refresh).sortedByDescending { it.id }.toImmutableList()
  }
}
