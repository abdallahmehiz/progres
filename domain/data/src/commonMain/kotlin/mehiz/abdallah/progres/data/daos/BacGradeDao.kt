package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.BacGradeTable
import mehiz.abdallah.progres.data.db.ProgresDB

class BacGradeDao(
  db: ProgresDB
) {
  val queries = db.bacGradeTableQueries

  fun insert(table: BacGradeTable) {
    queries.insert(table.subjectName, table.grade)
  }

  fun getAllBacGrades(): List<BacGradeTable> {
    return queries.get().executeAsList()
  }

  fun deleteAllBacGrades() {
    queries.deleteAllBacGrades()
  }
}
