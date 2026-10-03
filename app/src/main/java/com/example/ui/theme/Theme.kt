package com.example.ui.theme

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
    primary = AxisPrimaryDark,
    onPrimary = AxisOnPrimaryDark,
    primaryContainer = AxisPrimaryContainerDark,
    onPrimaryContainer = AxisOnPrimaryContainerDark,
    secondary = AxisSecondary,
    onSecondary = AxisOnSecondary,
    secondaryContainer = AxisSecondaryContainer,
    onSecondaryContainer = AxisOnSecondaryContainer,
    tertiary = AxisTertiary,
    onTertiary = AxisOnTertiary,
    background = AxisBackgroundDark,
    onBackground = AxisOnSurfaceDark,
    surface = AxisSurfaceDark,
    onSurface = AxisOnSurfaceDark,
    surfaceVariant = AxisSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = AxisOutlineDark,
    error = AxisDanger
)

private val LightColorScheme = lightColorScheme(
    primary = AxisPrimary,
    onPrimary = AxisOnPrimary,
    primaryContainer = AxisPrimaryContainer,
    onPrimaryContainer = AxisOnPrimaryContainer,
    secondary = AxisSecondary,
    onSecondary = AxisOnSecondary,
    secondaryContainer = AxisSecondaryContainer,
    onSecondaryContainer = AxisOnSecondaryContainer,
    tertiary = AxisTertiary,
    onTertiary = AxisOnTertiary,
    tertiaryContainer = AxisTertiaryContainer,
    onTertiaryContainer = AxisOnTertiaryContainer,
    background = AxisBackground,
    onBackground = AxisOnBackground,
    surface = AxisSurface,
    onSurface = AxisOnSurface,
    surfaceVariant = AxisSurfaceVariant,
    onSurfaceVariant = AxisOnSurfaceVariant,
    outline = AxisOutline,
    outlineVariant = AxisOutlineVariant,
    error = AxisDanger
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand identity by default
    content: @Composable () -> Unit,
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
