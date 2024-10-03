package presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import ui.home.examsschedule.DaysOfWeekTitle

@Composable
fun TimeTableWithGrid(
  startHour: LocalTime,
  endHour: LocalTime,
  days: ImmutableList<DayOfWeek>,
  events: ImmutableList<TimeTableEventData>,
  modifier: Modifier = Modifier,
  hourHeight: Dp = 60.dp,
) {
  Column(
    modifier.fillMaxSize(),
  ) {
    Row {
      Spacer(Modifier.fillMaxWidth(1f / (days.size + 1)))
      DaysOfWeekTitle(days)
    }
    HorizontalDivider()
    Row {
      TimeTableTimeColumn(
        startHour = startHour,
        endHour = endHour,
        hourHeight = hourHeight,
        modifier = Modifier
          .fillMaxWidth(1f / (days.size + 1)),
      )
      Box(
        Modifier
          .fillMaxHeight()
          .verticalScroll(rememberScrollState()),
      ) {
        TimeTableGrid(
          startHour = startHour,
          endHour = endHour,
          hourHeight = hourHeight,
          days = days,
        )
        TimeTable(
          startHour = startHour,
          endHour = endHour,
          days = days,
          events = events,
          hourHeight = hourHeight,
        )
      }
    }
  }
}

@Composable
fun TimeTableTimeColumn(
  startHour: LocalTime,
  endHour: LocalTime,
  hourHeight: Dp,
  modifier: Modifier = Modifier,
) {
  val hourCount = endHour.hour - startHour.hour

  Column(modifier = modifier.height((hourHeight * hourCount).coerceAtLeast(72.dp))) {
    for (hour in startHour.hour..endHour.hour) {
      Text(
        text = "$hour:00",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.End,
        modifier = Modifier
          .fillMaxWidth()
          .height(hourHeight)
          .padding(horizontal = 4.dp),
      )
    }
  }
}

@Composable
fun TimeTableGrid(
  startHour: LocalTime,
  endHour: LocalTime,
  hourHeight: Dp,
  days: ImmutableList<DayOfWeek>,
  modifier: Modifier = Modifier,
) {
  val hourCount = endHour.hour - startHour.hour

  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height((hourHeight * hourCount).coerceAtLeast(72.dp)),
  ) {
    val heightPerHour = hourHeight.toPx()

    days.forEachIndexed { index, _ ->
      drawLine(
        color = Color.Gray,
        start = Offset(x = (index * size.width) / days.size, y = 0f),
        end = Offset(x = (index * size.width) / days.size, y = size.height),
        strokeWidth = 1f,
      )
    }

    for (hour in startHour.hour..endHour.hour) {
      drawLine(
        color = Color.Gray,
        start = Offset(x = 0f, y = heightPerHour * (hour - startHour.hour)),
        end = Offset(x = size.width, y = heightPerHour * (hour - startHour.hour)),
        strokeWidth = 1f,
      )
    }
  }
}

@Composable
fun TimeTable(
  startHour: LocalTime,
  endHour: LocalTime,
  days: ImmutableList<DayOfWeek>,
  events: ImmutableList<TimeTableEventData>,
  hourHeight: Dp,
  modifier: Modifier = Modifier,
) {
  TimeTableLayout(
    startHour = startHour,
    endHour = endHour,
    days = days,
    events = events,
    hourHeight = hourHeight,
    modifier = modifier,
    content = {
      events.forEach { event ->
        event.content()
      }
    },
  )
}

@Composable
fun TimeTableLayout(
  startHour: LocalTime,
  endHour: LocalTime,
  days: ImmutableList<DayOfWeek>,
  events: ImmutableList<TimeTableEventData>,
  content: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  hourHeight: Dp = 60.dp,
) {
  Layout(
    modifier = modifier,
    content = content,
    measurePolicy = { measurables, constraints ->
      val hourCount = endHour.hour - startHour.hour
      val dayWidth = constraints.maxWidth / days.size
      val totalHeight = (hourHeight * hourCount).roundToPx().coerceAtLeast(72.dp.toPx().toInt())

      val placeables = measurables.mapIndexed { index, measurable ->
        val event = events[index]

        val startMinuteOfDay = event.startTime.hour * 60 + event.startTime.minute
        val endMinuteOfDay = event.endTime.hour * 60 + event.endTime.minute
        val eventHeight = ((endMinuteOfDay - startMinuteOfDay) / 60f * hourHeight.toPx()).toInt()
        val validEventHeight = eventHeight.coerceAtLeast(0)

        measurable.measure(
          constraints.copy(
            minHeight = validEventHeight,
            minWidth = constraints.maxWidth / days.size,
            maxHeight = validEventHeight,
            maxWidth = constraints.maxWidth / days.size,
          ),
        )
      }

      layout(
        width = constraints.maxWidth,
        height = totalHeight,
      ) {
        events.forEachIndexed { index, event ->
          val dayIndex = days.indexOf(event.day)

          val startMinuteOfDay = event.startTime.hour * 60 + event.startTime.minute
          val startY = ((startMinuteOfDay - startHour.hour * 60) / 60f * hourHeight.toPx()).toInt()

          placeables[index].place(
            x = dayWidth * dayIndex,
            y = startY,
          )
        }
      }
    },
  )
}

data class TimeTableEventData(
  val startTime: LocalTime,
  val endTime: LocalTime,
  val day: DayOfWeek,
  val content: @Composable () -> Unit,
)
