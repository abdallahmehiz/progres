package ui.home.transcriptsscreen

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
import mehiz.abdallah.progres.domain.models.AcademicDecisionModel
import mehiz.abdallah.progres.domain.models.TranscriptModel
import presentation.utils.RequestState

class TranscriptsScreenViewModel(
  accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _transcripts =
    MutableStateFlow<RequestState<ImmutableMap<String, Pair<AcademicDecisionModel?, List<TranscriptModel>>>>>(
      RequestState.Loading,
    )
  val transcripts = _transcripts.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _transcripts.update { _ ->
        try {
          val decisions = accountUseCase.getAllAcademicDecisions().sortedBy { it.id }
          val transcripts = accountUseCase.getAllTranscripts().filterNot { it.period == null }.sortedBy { it.id }

          val decisionsByYear = decisions.groupBy {
            it.period.academicYearStringLatin
          }
          val transcriptsByYear = transcripts.groupBy { it.period!!.academicYearStringLatin }

          RequestState.Success(
            transcriptsByYear.mapValues { (year, yearTranscripts) ->
              val decisionForYear = decisionsByYear[year]?.firstOrNull()
              Pair(decisionForYear, yearTranscripts)
            }.toImmutableMap(),
          )
        } catch (e: Exception) {
          RequestState.Error(e.message!!)
        }
      }
    }
  }
}
