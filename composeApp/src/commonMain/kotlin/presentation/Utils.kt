package presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

expect val ScreenWidthPixels: @Composable () -> Int

expect val ScreenHeightPixels: @Composable () -> Int

expect val ScreenHeightDp: @Composable () -> Dp

expect val ScreenWidthDp: @Composable () -> Dp
