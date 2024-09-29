package ui.home.transcriptsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.AcademicDecisionModel
import mehiz.abdallah.progres.domain.models.TranscriptModel

class TranscriptsScreenViewModel(
  accountUseCase: AccountUseCase,
) : ViewModel() {

  private val _transcripts =
    MutableStateFlow<Map<String, Pair<AcademicDecisionModel?, List<TranscriptModel>>>>(emptyMap())
  val transcripts = _transcripts.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      val decisions = accountUseCase.getAllAcademicDecisions()
        .sortedBy { it.id }
      val transcripts = accountUseCase.getAllTranscripts()
        .filterNot { it.period == null }
        .sortedBy { it.id }

      val decisionsByYear = decisions.groupBy {
        it.period.academicYearStringLatin
      }
      val transcriptsByYear = transcripts.groupBy { it.period!!.academicYearStringLatin }

      _transcripts.update {
        transcriptsByYear.mapValues { (year, yearTranscripts) ->
          val decisionForYear = decisionsByYear[year]?.firstOrNull()
          Pair(decisionForYear, yearTranscripts)
        }
      }
    }
  }
}
