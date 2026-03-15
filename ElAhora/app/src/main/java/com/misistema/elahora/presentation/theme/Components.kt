package com.misistema.elahora.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    contentColor: Color = Black,
    shape: Shape = RoundedCornerShape(0.dp),
    borderWidth: Dp = 3.dp,
    shadowOffset: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        // Flat Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(start = shadowOffset, top = shadowOffset)
                .background(Black, shape)
        )
        
        // Main Content Card
        Card(
            modifier = Modifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = backgroundColor, contentColor = contentColor),
            border = BorderStroke(borderWidth, Black)
        ) {
            content()
        }
    }
}

@Composable
fun NeuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    contentColor: Color = Black,
    borderWidth: Dp = 3.dp,
    shadowOffset: Dp = 4.dp
) {
    Box(modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(start = shadowOffset, top = shadowOffset)
                .background(Black)
        )

        Button(
            onClick = onClick,
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            ),
            border = BorderStroke(borderWidth, Black),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            modifier = Modifier
        ) {
            Text(
                text = text,
                style = Typography.labelSmall
            )
        }
    }
}
