package com.misistema.elahora.domain.model

data class Sistema(
    val id: String,
    val nombre: String,
    val icon: String,
    val quote: String,
    val identidad: String,
    val accionIdeal: String,
    val accionDiminuta: String,
    val miniRutina: MiniRutina,
    val fases: Fases
)

data class MiniRutina(
    val nombre: String,
    val duracionMin: Int,
    val pasos: List<String>
)

data class Fases(
    val actual: Int,
    val total: Int
)
