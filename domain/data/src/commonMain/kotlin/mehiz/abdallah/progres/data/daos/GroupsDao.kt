package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.GroupTable
import mehiz.abdallah.progres.data.db.ProgresDB

class GroupsDao(
  db: ProgresDB
) {
  private val queries = db.groupTableQueries

  fun insert(group: GroupTable) {
    with(group) {
      queries.insert(
        id = id,
        academicGroupStringLatin = academicGroupStringLatin,
        academicSectionStringLatin = academicSectionStringLatin,
        periodId = periodId,
        periodStringLatin = periodStringLatin
      )
    }
  }

  fun getAllGroups(): List<GroupTable> {
    return queries.getAllGroups().executeAsList()
  }

  fun deleteAllGroups() {
    queries.deleteAllGroups()
  }
}
