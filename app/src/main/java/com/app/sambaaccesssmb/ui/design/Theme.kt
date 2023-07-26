package com.app.sambaaccesssmb.ui.design

import android.content.res.Configuration
import android.os.Build.VERSION_CODES.S
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.utils.Build.has

/**
 * Multipreview annotation that represents light and dark themes. Add this annotation to a
 * composable to render the both themes.
 */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class ThemePreviews

@Composable
fun SmbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    androidTheme: Boolean = false,
    content:
    @Composable()
    () -> Unit,
) {
    val colorScheme = when {
        dynamicColor -> {
            if (has(S)) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }
        androidTheme -> if (darkTheme) DefaultDarkAndroidColorScheme else DefaultAndroidLightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    val defaultGradientColors = GradientColors()
    val gradientColors = when {
        dynamicColor -> {
            if (has(S)) {
                defaultGradientColors
            } else {
                if (darkTheme) defaultGradientColors else LightDefaultGradientColors
            }
        }
        androidTheme -> defaultGradientColors
        else -> if (darkTheme) defaultGradientColors else LightDefaultGradientColors
    }

    val defaultBackgroundTheme = BackgroundTheme(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    )

    val backgroundTheme = when {
        dynamicColor -> defaultBackgroundTheme
        androidTheme -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        else -> defaultBackgroundTheme
    }

    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SmbTypography,
            content = content,
        )
    }
}
