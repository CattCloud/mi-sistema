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
import com.misistema.elahora.presentation.theme.NeuCard
import com.misistema.elahora.presentation.theme.Typography
import com.misistema.elahora.presentation.theme.Violet
import com.misistema.elahora.presentation.theme.White

@Composable
fun AccionCard(
    accionDiminuta: String,
    pasos: List<String>,
    modifier: Modifier = Modifier
) {
    NeuCard(
        backgroundColor = Violet,
        contentColor = White,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚡ ACCIÓN DIMINUTA",
                style = Typography.labelSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                Spacer(modifier = Modifier.height(4.dp))
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
