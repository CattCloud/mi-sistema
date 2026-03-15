package com.misistema.elahora.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.misistema.elahora.presentation.theme.Black
import com.misistema.elahora.presentation.theme.Cyan
import com.misistema.elahora.presentation.theme.Typography
import com.misistema.elahora.presentation.theme.White

@Composable
fun ProgresoFases(
    faseActual: Int,
    totalFases: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalFases) {
            val isActive = i == faseActual
            val bgColor = if (isActive) Cyan else White
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(32.dp)
                    .background(bgColor, CircleShape)
                    .border(3.dp, Black, CircleShape)
            ) {
                Text(
                    text = "$i",
                    style = Typography.labelSmall,
                    color = Black
                )
            }
            
            if (i < totalFases) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 3.dp)
                        .background(Black)
                )
            }
        }
    }
}
