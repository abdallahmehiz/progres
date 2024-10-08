package ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Note
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarViewMonth
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MotionPhotosPause
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource
import mehiz.abdallah.progres.i18n.MR
import ui.home.accommodation.AccommodationScreen
import ui.home.ccgrades.CCGradesScreen
import ui.home.discharge.DischargeScreen
import ui.home.enrollments.EnrollmentsScreen
import ui.home.examgrades.ExamGradesScreen
import ui.home.examsschedule.ExamsScheduleScreen
import ui.home.groups.GroupsScreen
import ui.home.restaurant.RestaurantScreen
import ui.home.subjects.SubjectsScreen
import ui.home.subjectsschedule.SubjectsScheduleScreen
import ui.home.transcripts.TranscriptScreen

data class SubScreen(
  val icon: ImageVector,
  val title: StringResource,
  val destination: Screen? = null,
  val enabled: Boolean = true,
  val isBeta: Boolean = false,
)

val screens = listOf(
  SubScreen(Icons.Rounded.MotionPhotosPause, MR.strings.home_discharge, DischargeScreen),
  SubScreen(Icons.Rounded.CalendarViewMonth, MR.strings.home_time_table, SubjectsScheduleScreen),
  SubScreen(Icons.Rounded.House, MR.strings.home_accommodation, AccommodationScreen),
  SubScreen(Icons.Rounded.People, MR.strings.home_group, GroupsScreen),
  SubScreen(Icons.Rounded.AccountTree, MR.strings.home_subjects, SubjectsScreen),
  SubScreen(Icons.Rounded.CalendarMonth, MR.strings.home_exams_schedule, ExamsScheduleScreen),
  SubScreen(Icons.Rounded.EditNote, MR.strings.home_exams_results, ExamGradesScreen),
  SubScreen(Icons.Rounded.DoneAll, MR.strings.home_continuous_eval, CCGradesScreen),
  SubScreen(Icons.Rounded.FolderCopy, MR.strings.home_academic_transcripts, TranscriptScreen),
  SubScreen(Icons.Rounded.Calculate, MR.strings.home_debts, enabled = false),
  SubScreen(Icons.AutoMirrored.Rounded.Note, MR.strings.home_academic_vacations, enabled = false),
  SubScreen(Icons.Rounded.Inventory2, MR.strings.home_enrollments, EnrollmentsScreen),
  // SubScreen(Icons.AutoMirrored.Rounded.FactCheck, MR.strings.home_bac_results, BacInfoScreen),
  SubScreen(Icons.Rounded.Restaurant, MR.strings.home_restaurant, RestaurantScreen, isBeta = true),
  SubScreen(Icons.Rounded.MoreHoriz, MR.strings.home_more_services, enabled = false),
)
