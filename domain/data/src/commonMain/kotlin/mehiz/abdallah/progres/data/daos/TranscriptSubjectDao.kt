package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.TranscriptSubjectTable

class TranscriptSubjectDao(
  db: ProgresDB
) {

  private val queries = db.transcriptSubjectTableQueries

  fun insert(table: TranscriptSubjectTable) {
    with(table) {
      queries.insert(
        id = id,
        subjectStringLatin = subjectStringLatin,
        subjectStringArabic = subjectStringArabic,
        coefficient = coefficient,
        creditObtained = creditObtained,
        ueId = ueId,
        average = average
      )
    }
  }

  fun getAllSubjects(): List<TranscriptSubjectTable> {
    return queries.getAllTranscriptSubjects().executeAsList()
  }

  fun getSubjectsByUEId(ueId: Long): List<TranscriptSubjectTable> {
    return queries.getTranscriptsSubjectsOfUE(ueId).executeAsList()
  }

  fun deleteAllSubjects() {
    queries.deleteAllTranscriptSubjects()
  }

  fun getSubjectById(id: Long): TranscriptSubjectTable? {
    return queries.getTranscriptSubjectById(id).executeAsOneOrNull()
  }
}
