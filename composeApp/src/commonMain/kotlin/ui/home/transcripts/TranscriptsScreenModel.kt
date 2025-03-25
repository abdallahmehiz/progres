package ui.home.transcripts

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.TranscriptUseCase
import mehiz.abdallah.progres.domain.models.AcademicDecisionModel
import mehiz.abdallah.progres.domain.models.TranscriptModel
import presentation.utils.RequestState

class TranscriptsScreenModel(
  private val transcriptUseCase: TranscriptUseCase,
) : ScreenModel {

  private val _transcripts =
    MutableStateFlow<RequestState<ImmutableMap<String, Pair<AcademicDecisionModel?, List<TranscriptModel>>>>>(
      RequestState.Loading,
    )
  val transcripts = _transcripts.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  init {
    screenModelScope.launch(Dispatchers.IO) {
      _transcripts.update { _ ->
        try {
          RequestState.Success(getData(false))
        } catch (e: Exception) {
          RequestState.Error(e)
        }
      }
    }
  }

  suspend fun refresh() {
    _transcripts.update { RequestState.Success(getData(true)) }
  }

  private suspend fun getData(refresh: Boolean):
    ImmutableMap<String, Pair<AcademicDecisionModel?, List<TranscriptModel>>> {
    val decisions = transcriptUseCase.getAllAcademicDecisions(refresh, refresh).sortedBy { it.period.id }
    val transcripts = transcriptUseCase.getAllTranscripts(refresh, false).sortedBy { it.period.id }

    val decisionsByYear = decisions.groupBy { it.period.academicYearCode }
    val transcriptsByYear = transcripts.groupBy { it.period.academicYearCode }

    return transcriptsByYear.mapValues { (year, yearTranscripts) ->
      val decisionForYear = decisionsByYear[year]?.firstOrNull()
      Pair(decisionForYear, yearTranscripts)
    }.toImmutableMap()
  }
}
