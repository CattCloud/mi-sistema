package com.misistema.elahora.domain.model

object SistemaParser {

    fun parse(id: String, markdown: String): Sistema {
        // Valores por defecto
        var nombre = id.replace("-", " ").replaceFirstChar { it.uppercase() }
        var mantra = ""
        var accionDiminuta = ""
        val pasos = mutableListOf<String>()
        var protocolo = ""
        var faseActual = 1

        val lines = markdown.lines()
        var currentSection = ""

        for (line in lines) {
            val trimmed = line.trim()
            
            // Detectar secciones H1 / H2
            if (trimmed.startsWith("# ")) {
                nombre = trimmed.removePrefix("# ").trim()
                continue
            }
            if (trimmed.startsWith("## ")) {
                currentSection = trimmed.removePrefix("## ").lowercase()
                continue
            }
            
            if (trimmed.isEmpty()) continue

            // Lógica por sección basada en plantilla-sistema.md
            when {
                currentSection.contains("nueva identidad") || currentSection.contains("mantra") -> {
                    if (trimmed.startsWith(">")) {
                        mantra += trimmed.removePrefix(">").trim() + " "
                    } else if (mantra.isEmpty() && !trimmed.startsWith("**")) {
                        mantra += "$trimmed " // Fallback
                    }
                }
                currentSection.contains("acción diminuta") -> {
                    if (trimmed.startsWith("-") || trimmed.startsWith("*")) {
                        if (accionDiminuta.isEmpty()) {
                            accionDiminuta = trimmed.removePrefix("-").removePrefix("*").trim()
                        }
                    } else if (accionDiminuta.isEmpty()) {
                        accionDiminuta = trimmed
                    }
                }
                currentSection.contains("pasos") || currentSection.contains("acción extendida") -> {
                    if (trimmed.startsWith("-") || Regex("^\\d+\\.").containsMatchIn(trimmed)) {
                        pasos.add(trimmed.replace(Regex("^[-*0-9.]+"), "").trim())
                    }
                }
                currentSection.contains("protocolo del peor día") -> {
                    if (trimmed.startsWith("-") || trimmed.startsWith(">") || !trimmed.startsWith("**")) {
                        protocolo += trimmed.removePrefix("-").removePrefix(">").trim() + " "
                    }
                }
            }
        }

        // Detectar si el nombre ya incluye un número y limpiar "01 -"
        nombre = nombre.replace(Regex("^\\d+\\s*-\\s*"), "")

        return Sistema(
            id = id,
            nombre = nombre,
            mantra = mantra.trim(),
            accionDiminuta = accionDiminuta.trim(),
            pasos = pasos,
            protocoloPeorDia = protocolo.trim(),
            faseActual = faseActual
        )
    }
}
