package com.example.onlineexaminationsystem.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
private val DarkColorScheme = darkColorScheme(
    // Primary actions (buttons, highlights)
    primary = Color(0xFF60A5FA),        // Soft blue (easy on eyes)
    onPrimary = Color(0xFF020617),

    // Secondary accents
    secondary = Color(0xFF2DD4BF),      // Teal
    onSecondary = Color(0xFF022C22),

    // Backgrounds
    background = Color(0xFF020617),     // Almost black (not pure)
    onBackground = Color(0xFFE5E7EB),

    // Surfaces (cards, text fields)
    surface = Color(0xFF0F172A),        // Dark blue-gray
    onSurface = Color(0xFFE5E7EB),

    // Error
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = TealSecondary,
    background = AppBackground,
    surface = CardBackground,
    error = ErrorRed,
    onPrimary = Color.White,
    onSurface = TextPrimary
)


@Composable
fun OnlineExaminationSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}