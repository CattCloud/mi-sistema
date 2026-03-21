package com.misistema.elahora.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// El tema dinámico que va a estar disponible en toda la UI
val LocalSystemTheme = staticCompositionLocalOf { DefaultSystemTheme }

@Composable
fun ElAhoraTheme(
    systemTheme: SystemTheme = DefaultSystemTheme,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgPage.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    CompositionLocalProvider(
        LocalSystemTheme provides systemTheme
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                background = BgPage,
                surface = CardSurface,
                onBackground = TextPrimary,
                onSurface = TextPrimary,
                primary = systemTheme.accentMain,
                onPrimary = Color.White
            ),
            typography = ElAhoraTypography,
            content = content
        )
    }
}
