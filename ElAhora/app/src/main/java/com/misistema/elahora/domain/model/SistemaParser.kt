package com.misistema.elahora.domain.model

import org.json.JSONObject
import androidx.compose.ui.graphics.Color
import com.misistema.elahora.presentation.theme.SystemTheme

object SistemaParser {
    fun parseJson(jsonString: String): Sistema {
        val root = JSONObject(jsonString)
        
        val rutina = root.getJSONObject("mini_rutina")
        val pasosArray = rutina.getJSONArray("pasos")
        val pasos = mutableListOf<String>()
        for(i in 0 until pasosArray.length()) {
            pasos.add(pasosArray.getString(i))
        }
        val miniRutina = MiniRutina(
            nombre = rutina.getString("nombre"),
            duracionMin = rutina.getInt("duracion_min"),
            pasos = pasos
        )
        
        val fasesObj = root.getJSONObject("fases")
        val fases = Fases(
            actual = fasesObj.getInt("actual"),
            total = fasesObj.getInt("total")
        )
        
        var theme: SystemTheme? = null
        if (root.has("theme")) {
            val tObj = root.getJSONObject("theme")
            theme = SystemTheme(
                accentMain = Color(android.graphics.Color.parseColor(tObj.getString("accentMain"))),
                accentDark = Color(android.graphics.Color.parseColor(tObj.getString("accentDark"))),
                accentLight = Color(android.graphics.Color.parseColor(tObj.getString("accentLight"))),
                accentMid = Color(android.graphics.Color.parseColor(tObj.getString("accentMid")))
            )
        }
        
        val protocolo = if (root.has("protocolo_peor_dia")) root.getString("protocolo_peor_dia") else "Si algo falla, haz lo mínimo indispensable."
        
        return Sistema(
            id = root.getString("id"),
            nombre = root.getString("nombre"),
            icon = root.getString("icon"),
            quote = root.getString("quote"),
            identidad = root.getString("identidad"),
            accionIdeal = root.getString("accion_ideal"),
            accionDiminuta = root.getString("accion_diminuta"),
            miniRutina = miniRutina,
            fases = fases,
            protocoloPeorDia = protocolo,
            theme = theme
        )
    }
}
