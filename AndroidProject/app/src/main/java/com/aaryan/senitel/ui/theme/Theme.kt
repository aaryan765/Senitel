package com.aaryan.senitel.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CtosColorScheme = darkColorScheme(
    primary = CtosBlue,
    onPrimary = Color.Black,
    primaryContainer = CtosDarkBlue,
    onPrimaryContainer = Color.White,
    secondary = CtosBlue,
    onSecondary = Color.Black,
    background = CtosBackground,
    onBackground = CtosTextPrimary,
    surface = CtosSurface,
    onSurface = CtosTextPrimary,
    error = CtosError,
    onError = Color.Black
)

@Composable
fun SenitelTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CtosBackground.toArgb()
            window.navigationBarColor = CtosBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = CtosColorScheme,
        typography = Typography,
        content = content
    )
}
