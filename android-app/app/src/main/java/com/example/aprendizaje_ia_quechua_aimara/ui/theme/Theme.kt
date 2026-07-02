package com.nescore.aprendizaje_ia_quechua_aimara.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CustomDarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryPurple,
    tertiary = Purple80,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextHighContrast,
    onSurface = TextHighContrast
)

@Composable
fun Aprendizaje_IA_Qechua_AimaraTheme(
    // Force dark theme as requested
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomDarkColorScheme,
        typography = Typography,
        content = content
    )
}
