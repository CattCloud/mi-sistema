package com.misistema.elahora.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DiasAnteriores(
    weekLogs: List<DailyLog>,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatLabel = SimpleDateFormat("E", Locale("es", "ES"))
    val formatFull = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    val todayDate = formatFull.format(Date())

    val cal = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "DÍAS ANTERIORES", style = Typography.labelSmall)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0..6) {
                val dateStr = formatFull.format(cal.time)
                val label = formatLabel.format(cal.time).uppercase().take(1)
                val log = weekLogs.find { it.date == dateStr }
                
                val isFuture = dateStr > todayDate
                val bgColor = when {
                    isFuture -> BgPage
                    log?.status == LogStatus.DONE -> AccentButton
                    log?.status == LogStatus.NOT_DONE -> AccentLight
                    else -> BgCard
                }
                
                val borderColor = when {
                    isFuture -> Color.Transparent
                    log?.status != null -> DividerColor
                    else -> DividerColor.copy(alpha = 0.5f)
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .size(40.dp)
                        .background(bgColor, RoundedCornerShape(4.dp))
                        .border(
                            width = 1.dp, 
                            color = borderColor, 
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable(enabled = !isFuture && dateStr != todayDate) {
                            onDayClick(dateStr)
                        }
                ) {
                    Text(
                        text = label,
                        style = Typography.labelSmall,
                        color = if (isFuture) TextSecondary else TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
                
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }
}
