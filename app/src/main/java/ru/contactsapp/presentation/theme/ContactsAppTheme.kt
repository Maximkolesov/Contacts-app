package ru.contactsapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BlueWhiteColorScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEAFE),
    onPrimaryContainer = Color(0xFF0F2F70),
    secondary = Color(0xFF2563EB),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFF6FF),
    onSecondaryContainer = Color(0xFF1E3A8A),
    background = Color(0xFFF8FBFF),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFEFF6FF),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFF93C5FD),
    outlineVariant = Color(0xFFBFDBFE),
    error = Color(0xFFB91C1C),
    onError = Color.White,
)

@Composable
fun ContactsAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlueWhiteColorScheme,
        content = content,
    )
}
