package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TuringColorScheme = darkColorScheme(
  primary = TuringCyan,
  onPrimary = TuringObsidian,
  primaryContainer = TuringObsidianCard,
  onPrimaryContainer = TuringCyan,
  secondary = TuringEmerald,
  onSecondary = TuringObsidian,
  secondaryContainer = TuringObsidianCard,
  onSecondaryContainer = TuringEmerald,
  tertiary = TuringPurple,
  onTertiary = Color.White,
  background = TuringObsidian,
  onBackground = TuringTextPrimary,
  surface = TuringObsidianSurface,
  onSurface = TuringTextPrimary,
  surfaceVariant = TuringObsidianCard,
  onSurfaceVariant = TuringTextSecondary,
  outline = TuringObsidianBorder,
  outlineVariant = TuringObsidianBorder,
  error = TuringCoral,
  onError = Color.White
)

@Composable
fun TuringTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = TuringColorScheme,
    typography = Typography,
    content = content
  )
}

