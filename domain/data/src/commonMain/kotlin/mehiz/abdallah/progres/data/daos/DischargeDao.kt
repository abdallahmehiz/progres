package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.DischargeTable
import mehiz.abdallah.progres.data.db.ProgresDB

class DischargeDao(
  db: ProgresDB
) {
  private val queries = db.dischargeTableQueries

  fun insert(table: DischargeTable) {
    with(table) {
      queries.insert(
        uuid = uuid,
        department = department,
        centralLibrary = centralLibrary,
        scholarship = scholarship,
        residence = residence,
        faculty = faculty
      )
    }
  }

  fun get(): DischargeTable? {
    return queries.get().executeAsOneOrNull()
  }

  fun delete() {
    queries.delete()
  }
}
