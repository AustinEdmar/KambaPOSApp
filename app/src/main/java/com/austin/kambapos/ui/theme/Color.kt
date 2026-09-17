package com.austin.kambapos.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Cor sólida (usada onde o Material3 exige um único Color, ex: primary)
val SunmiOrange = Color(0xFF3554C1)

// Gradiente linear vertical (topo -> baixo), igual à imagem
val SunmiGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF3554C1),
        Color(0xFF3DDBBF)
    )
)

// Versão horizontal, caso precises
val SunmiGradientHorizontal = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF3554C1),
        Color(0xFF3DDBBF)
    )
)

val CardBg = Color(0xFFFFFFFF)
val TextMain = Color(0xFF1A1A2E)
val TextMuted = Color(0xFF9E9E9E)
val DividerColor = Color(0xFFEEEEEE)

