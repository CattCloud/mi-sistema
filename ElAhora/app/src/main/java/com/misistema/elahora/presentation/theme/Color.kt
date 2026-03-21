package com.misistema.elahora.presentation.theme

import androidx.compose.ui.graphics.Color

// ============================================
// 1. COLORES BASE (Fijos para toda la app)
// ============================================

/** Fondo base frío y neutro para la pantalla principal. */
val BgPage = Color(0xFFF7F7F9)

/** Blanco puro para todas las cards flotantes. */
val CardSurface = Color(0xFFFFFFFF)

/** Texto casi negro azulado para alta legibilidad. */
val TextPrimary = Color(0xFF1A1A2E)

// ============================================
// 2. COLORES SEMÁNTICOS (Fijos)
// ============================================

/** Card "Peor Día" al final del listado - Ámbar puro */
val AmberBg = Color(0xFFFFF8EC)
val AmberText = Color(0xFF8B5E10)
val AmberBody = Color(0xFF7A5218)
val AmberIcon = Color(0xFFB07A20)

/** Estado: Cumplí */
val StateDoneBg = Color(0xFFD4F0E0)
val StateDoneText = Color(0xFF1A5C38)

/** Estado: No cumplí */
val StateNotDoneBg = Color(0xFFFAD8D8)
val StateNotDoneText = Color(0xFF8B2020)

// ============================================
// 3. ESTRUCTURA DEL TEMA DINÁMICO
// ============================================

/**
 * Cada sistema inyecta sus propios 4 colores. 
 * El LocalSystemTheme proveerá esta instancia.
 */
data class SystemTheme(
    val accentMain: Color,  // Botones, rings activos
    val accentDark: Color,   // Fondo de Identidad
    val accentLight: Color,  // Hover, círculos sin marcar
    val accentMid: Color     // Hints, labels mayúsculas
)

// Este es el tema base por defecto (Sistema 01 - Sueño)
val DefaultSystemTheme = SystemTheme(
    accentMain = Color(0xFF7B5EA7),
    accentDark = Color(0xFF3D2B6B),
    accentLight = Color(0xFFEDE8F5),
    accentMid = Color(0xFF9B85C4)
)
