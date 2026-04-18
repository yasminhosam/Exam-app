package com.example.onlineexaminationsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary
val BluePrimary = Color(0xFF2563EB)   // Main actions
val BlueDark = Color(0xFF1E40AF)

// Secondary
val TealSecondary = Color(0xFF14B8A6)

// Backgrounds
val AppBackground = Color(0xFFF8FAFC)
val CardBackground = Color(0xFFFFFFFF)

// Text
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)

// States
val SuccessGreen = Color(0xFF22C55E)
val ErrorRed = Color(0xFFEF4444)
val WarningOrange = Color(0xFFF59E0B)

// --- Custom Logic: Light Theme Colors ---
val CorrectGreenLight = Color(0xFF2E7D32)
val CorrectGreenBgLight = Color(0xFFE8F5E9)

val WrongRedLight = Color(0xFFC62828)
val WrongRedBgLight = Color(0xFFFFEBEE)

// --- Custom Logic: Dark Theme Colors ---
val CorrectGreenDark = Color(0xFF81C784)
val CorrectGreenBgDark = Color(0xFF0F3B1A)

val WrongRedDark = Color(0xFFE57373)
val WrongRedBgDark = Color(0xFF4A0E0E)


object ExamAppColors {
    val correctText: Color
        @Composable
        get() = if (isSystemInDarkTheme()) CorrectGreenDark else CorrectGreenLight

    val correctBackground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) CorrectGreenBgDark else CorrectGreenBgLight

    val wrongText: Color
        @Composable
        get() = if (isSystemInDarkTheme()) WrongRedDark else WrongRedLight

    val wrongBackground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) WrongRedBgDark else WrongRedBgLight
}