package com.misistema.elahora.presentation.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ElAhoraCard(
    modifier: Modifier = Modifier,
    containerColor: Color = CardSurface,
    contentColor: Color = TextPrimary,
    elevation: Dp = 2.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.05f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(18.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ElAhoraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    textColor: Color = Color.White,
    elevation: Dp = 3.dp,
    isActive: Boolean = true
) {
    val bg = backgroundColor ?: LocalSystemTheme.current.accentMain
    
    Button(
        onClick = onClick,
        enabled = isActive,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = textColor,
            disabledContainerColor = bg.copy(alpha = 0.5f),
            disabledContentColor = textColor.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElAhoraInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = LocalSystemTheme.current.accentMid, style = MaterialTheme.typography.bodyLarge) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = BgPage,
            focusedContainerColor = BgPage,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = LocalSystemTheme.current.accentMain,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = modifier.fillMaxWidth()
    )
}
