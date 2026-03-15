package com.misistema.elahora.presentation.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.misistema.elahora.presentation.theme.NeuCard
import com.misistema.elahora.presentation.theme.Typography
import com.misistema.elahora.presentation.theme.Yellow

@Composable
fun MantraCard(mantra: String, modifier: Modifier = Modifier) {
    NeuCard(
        backgroundColor = Yellow,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "«$mantra»",
            style = Typography.titleLarge,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(16.dp)
        )
    }
}
