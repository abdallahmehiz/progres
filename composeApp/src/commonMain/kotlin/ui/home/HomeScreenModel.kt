package ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.liftric.kvault.KVault
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.core.TAG
import mehiz.abdallah.progres.domain.AccommodationUseCase
import mehiz.abdallah.progres.domain.BacInfoUseCase
import mehiz.abdallah.progres.domain.StudentCardUseCase
import mehiz.abdallah.progres.domain.SubjectScheduleUseCase
import mehiz.abdallah.progres.domain.UserAuthUseCase
import mehiz.abdallah.progres.domain.models.AccommodationModel
import mehiz.abdallah.progres.domain.models.BacInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import preferences.KVaultKeys
import presentation.utils.RequestState

data class HomeScreenUIData(
  val studentCard: StudentCardModel,
  val bacInfo: BacInfoModel,
  val accommodationStateModel: AccommodationModel? = null,
)

class HomeScreenModel(
  private val studentCardUseCase: StudentCardUseCase,
  private val bacInfoUseCase: BacInfoUseCase,
  private val subjectScheduleUseCase: SubjectScheduleUseCase,
  private val accommodationUseCase: AccommodationUseCase,
  private val authUseCase: UserAuthUseCase,
  private val kVault: KVault,
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
          Napier.e(tag = TAG, throwable = e) { e.stackTraceToString() }
          RequestState.Error(e)
        }
      }
      _data.value.getSuccessDataOrNull()?.let {
        try {
          _nextSchedule.update { getNextSubjectSchedule() }
        } catch (e: Exception) {
          Napier.e(tag = TAG, throwable = e) { e.stackTraceToString() }
        }
      }
      _data.value.getSuccessDataOrNull()?.let {
        try {
          _data.update { _ ->
            getAccommodationState(it.studentCard.id)?.let { state ->
              RequestState.Success(it.copy(accommodationStateModel = state))
            } ?: return@let
          }
        } catch (e: Exception) {
          Napier.e(tag = TAG, throwable = e) { e.stackTraceToString() }
        }
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

  private suspend fun getAccommodationState(cardId: Long): AccommodationModel? {
    return accommodationUseCase.getAccommodationStateForCard(cardId)
  }

  private suspend fun getData(refresh: Boolean): HomeScreenUIData {
    return HomeScreenUIData(
      studentCard = studentCardUseCase.getLatestStudentCard(refresh),
      bacInfo = bacInfoUseCase.getBacInfoWithGrades(refresh),
    )
  }

  // in case the worker didn't/couldn't refresh the token
  // try to refresh it manually.
  // in case that also fails, prompt the user to re-login
  // called from ui so it's possible to navigate
  suspend fun tryRefreshToken() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    if (authUseCase.getExpirationDate().date.toEpochDays() < now.date.toEpochDays()) {
      val id = kVault.string(KVaultKeys.id)
      checkNotNull(id) { "Vault doesn't contain id" }
      val password = kVault.string(KVaultKeys.password)
      checkNotNull(password) { "Vault doesn't contain password" }
      authUseCase.refreshLogin(id, password)
    }
  }
}
