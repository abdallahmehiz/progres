package ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.BacInfoUseCase
import mehiz.abdallah.progres.domain.StudentCardUseCase
import mehiz.abdallah.progres.domain.SubjectScheduleUseCase
import mehiz.abdallah.progres.domain.models.AccommodationStateModel
import mehiz.abdallah.progres.domain.models.BacInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import presentation.utils.RequestState

data class HomeScreenUIData(
  val studentCard: StudentCardModel,
  val bacInfo: BacInfoModel,
  val accommodationStateModel: AccommodationStateModel? = null,
)

class HomeScreenModel(
  private val studentCardUseCase: StudentCardUseCase,
  private val bacInfoUseCase: BacInfoUseCase,
  private val subjectScheduleUseCase: SubjectScheduleUseCase,
) : ScreenModel {

  private val _data = MutableStateFlow<RequestState<HomeScreenUIData>>(RequestState.Loading)
  val data = _data.asStateFlow()

  private val _nextSchedule = MutableStateFlow<SubjectScheduleModel?>(null)
  val nextSchedule = _nextSchedule.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _data.update {
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
      _data.value.getSuccessDataOrNull()?.let {
        runCatching {
          _data.update { _ ->
            getAccommodationState(it.studentCard.id)?.let { state ->
              RequestState.Success(it.copy(accommodationStateModel = state))
            } ?: return@let
          }
        }
      }
      _data.value.getSuccessDataOrNull()?.let {
        runCatching { _nextSchedule.update { getNextSubjectSchedule() } }
      }
    }
  }

  suspend fun refresh() {
    _data.update { RequestState.Success(getData(true)) }
    _data.value.getSuccessDataOrNull()?.let {
      runCatching {
        _data.update { _ ->
          RequestState.Success(it.copy(accommodationStateModel = getAccommodationState(it.studentCard.id)))
        }
      }
    }
    _nextSchedule.update { getNextSubjectSchedule() }
  }

  private suspend fun getNextSubjectSchedule(): SubjectScheduleModel? {
    return subjectScheduleUseCase.getNextSubjectSchedule()
  }

  private suspend fun getAccommodationState(cardId: Long): AccommodationStateModel? {
    return studentCardUseCase.getAccommodationStateForCard(cardId)
  }

  private suspend fun getData(refresh: Boolean): HomeScreenUIData {
    return HomeScreenUIData(
      studentCard = studentCardUseCase.getLatestStudentCard(refresh),
      bacInfo = bacInfoUseCase.getBacInfoWithGrades(refresh),
    )
  }
}
