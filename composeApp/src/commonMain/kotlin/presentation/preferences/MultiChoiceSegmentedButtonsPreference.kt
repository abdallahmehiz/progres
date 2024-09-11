package presentation.preferences

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultiChoiceSegmentedButtonsPreference(
  choices: ImmutableList<T>,
  selectedIndices: ImmutableList<Int>,
  onClick: (T) -> Unit,
  modifier: Modifier = Modifier,
  valueToText: (T) -> String = { it.toString() },
) {
  MultiChoiceSegmentedButtonRow(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
  ) {
    choices.forEachIndexed { index, choice ->
      SegmentedButton(
        checked = selectedIndices.contains(index),
        onCheckedChange = { onClick(choice) },
        shape = SegmentedButtonDefaults.itemShape(index = index, count = choices.size),
      ) {
        Text(text = valueToText(choice))
      }
    }
  }
}
