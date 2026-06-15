package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme =
  darkColorScheme(
      primary = QuantumPrimary,
      secondary = QuantumSecondary,
      tertiary = QuantumTertiary,
      background = QuantumBackground,
      surface = QuantumSurface,
      onBackground = QuantumText,
      onSurface = QuantumText
  )

private val LightColorScheme =
  lightColorScheme(
      primary = QuantumPrimaryLight,
      secondary = QuantumSecondaryLight,
      tertiary = QuantumTertiary,
      background = QuantumBackgroundLight,
      surface = QuantumSurfaceLight,
      onBackground = QuantumTextLight,
      onSurface = QuantumTextLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}
