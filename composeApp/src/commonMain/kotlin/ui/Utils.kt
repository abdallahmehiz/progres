package ui

import androidx.compose.runtime.Composable

expect val ScreenWidthPixels: @Composable () -> Int

expect val ScreenHeightPixels: @Composable () -> Int
