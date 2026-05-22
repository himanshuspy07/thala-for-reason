package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThalaTheme {
    NAVY_GOLD,
    CS_YELLOW,
    INDIA_BLUE,
    NEON_MINT
}

data class ThalaColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val neonGlow: Color
)

object ThalaThemeConfig {

    fun getColors(theme: ThalaTheme): ThalaColors {
        return when (theme) {
            ThalaTheme.NAVY_GOLD -> ThalaColors(
                background = Color(0xFF040815),      // Sophisticated Dark background
                surface = Color(0xFF0B1229),         // Slate dark blended container surface
                surfaceVariant = Color(0xFF1E293B),  // Slate-800 component background
                primary = Color(0xFFFFD700),          // Radiant Gold (with drop shadows)
                primaryVariant = Color(0xFFB8860B),   // Sophisticated Brushed Goldenrod
                secondary = Color(0xFF10B981),        // Success dynamic green turf accent
                accent = Color(0xFF6366F1),           // Midnight Indigo accent
                textPrimary = Color(0xFFF1F5F9),      // Slate-100 high-readability primary text
                textSecondary = Color(0xFF94A3B8),    // Slate-400 secondary text
                neonGlow = Color(0x33FFD700)          // Radiant glow around #7 Active metrics
            )
            ThalaTheme.CS_YELLOW -> ThalaColors(
                background = Color(0xFF090A11),
                surface = Color(0xFFF9DC02),         // Bright Chennai Super Kings yellow
                surfaceVariant = Color(0xFF1E233D),  // Dark Contrast surface
                primary = Color(0xFFF6C302),
                primaryVariant = Color(0xFF0066CC),   // Chennai Blue secondary
                secondary = Color(0xFF0072FF),
                accent = Color(0xFFFF5722),
                textPrimary = Color(0xFFF9FAFB),
                textSecondary = Color(0xFFB1B6CE),
                neonGlow = Color(0x7FFFFD00)
            )
            ThalaTheme.INDIA_BLUE -> ThalaColors(
                background = Color(0xFF000814),
                surface = Color(0xFF001D3D),          // India deep royal blue
                surfaceVariant = Color(0xFF003566),
                primary = Color(0xFFFF9933),          // Saffron orange primary
                primaryVariant = Color(0xFF000814),
                secondary = Color(0xFF1E3A8A),
                accent = Color(0xFF10B981),
                textPrimary = Color(0xFFFFFFFF),
                textSecondary = Color(0xFF94A3B8),
                neonGlow = Color(0x33FF9933)
            )
            ThalaTheme.NEON_MINT -> ThalaColors(
                background = Color(0xFF020617),
                surface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFF1E293B),
                primary = Color(0xFF22C55E),          // Glowing emerald green
                primaryVariant = Color(0xFF14532D),
                secondary = Color(0xFF06B6D4),
                accent = Color(0xFFF43F5E),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFF64748B),
                neonGlow = Color(0x3322C55E)
            )
        }
    }
}
