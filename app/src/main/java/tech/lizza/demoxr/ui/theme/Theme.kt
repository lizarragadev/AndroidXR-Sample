package tech.lizza.demoxr.ui.theme

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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    // Colores principales basados en el poster DevFest
    primary = DevFestBlue,
    onPrimary = Color.White,
    primaryContainer = DevFestBlueLight,
    onPrimaryContainer = DevFestOnSurface,
    
    secondary = DevFestOrange,
    onSecondary = Color.White,
    secondaryContainer = DevFestOrangeLight,
    onSecondaryContainer = DevFestOnSurface,
    
    tertiary = DevFestPink,
    onTertiary = Color.White,
    tertiaryContainer = DevFestPinkLight,
    onTertiaryContainer = DevFestOnSurface,
    
    // Fondos basados en el poster
    background = DevFestBackground,
    onBackground = DevFestOnBackground,
    surface = DevFestSurface,
    onSurface = DevFestOnSurface,
    surfaceVariant = DevFestBackground,
    onSurfaceVariant = DevFestOnSurface,
    
    // Colores de contenedores
    surfaceContainer = DevFestSurface,
    surfaceContainerHigh = DevFestSurface,
    surfaceContainerHighest = DevFestSurface,
    
    // Colores de error y outline
    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    
    outline = DevFestBlue,
    outlineVariant = DevFestBlueLight
)

@Composable
fun DemoXRTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Deshabilitado para usar nuestros colores DevFest
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