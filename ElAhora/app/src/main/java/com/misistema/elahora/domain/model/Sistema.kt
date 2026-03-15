package com.misistema.elahora.domain.model

data class Sistema(
    val id: String, // ej: "01-calidad-sueno"
    val nombre: String,
    val mantra: String,
    val accionDiminuta: String,
    val pasos: List<String>,
    val protocoloPeorDia: String,
    val faseActual: Int = 1,
    val totalFases: Int = 3
)
