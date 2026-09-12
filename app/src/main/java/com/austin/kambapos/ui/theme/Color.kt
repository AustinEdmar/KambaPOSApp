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

val OrangePrimary = Color(0xFFEFD7C5) // Laranja
val OrangeOnPrimary = Color(0xFFFFFFFF) // Texto/ícone em cima do laranja

val yellow = Color(0xFFFFC107) // Laranja claro

val green = Color(0xFF8BC34A) // Verde

val red = Color(0xFFF44336) // Vermelho