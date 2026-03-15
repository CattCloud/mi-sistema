package com.misistema.elahora.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.misistema.elahora.presentation.theme.*

@Composable
fun AccionCard(
    accionDiminuta: String,
    pasos: List<String>,
    modifier: Modifier = Modifier
) {
    HenryCard(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "⚡ ACCIÓN DIMINUTA",
                style = Typography.labelSmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = accionDiminuta,
                style = Typography.titleLarge
            )
            
            if (pasos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "PASOS A SEGUIR:",
                    style = Typography.labelSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                pasos.forEach { paso ->
                    Text(
                        text = "• $paso",
                        style = Typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
