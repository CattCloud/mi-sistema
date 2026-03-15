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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.presentation.theme.Black
import com.misistema.elahora.presentation.theme.Cyan
import com.misistema.elahora.presentation.theme.GrayMuted
import com.misistema.elahora.presentation.theme.Typography
import com.misistema.elahora.presentation.theme.White
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
    val formatLabel = SimpleDateFormat("E", Locale("es", "ES")) // "lun", "mar"
    val formatFull = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    val todayDate = formatFull.format(Date())

    val cal = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Días Anteriores", style = Typography.labelSmall)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0..6) {
                val dateStr = formatFull.format(cal.time)
                val label = formatLabel.format(cal.time).uppercase().take(1)
                val log = weekLogs.find { it.date == dateStr }
                
                val isFuture = dateStr > todayDate
                val bgColor = when {
                    isFuture -> GrayMuted.copy(alpha = 0.3f)
                    log?.status == LogStatus.DONE -> Cyan
                    else -> White
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .size(40.dp)
                        .background(bgColor, RoundedCornerShape(4.dp))
                        .border(
                            width = if (isFuture) 1.dp else 2.dp, 
                            color = if (isFuture) GrayMuted else Black, 
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable(enabled = !isFuture && dateStr != todayDate) {
                            onDayClick(dateStr)
                        }
                ) {
                    Text(
                        text = label,
                        style = Typography.labelSmall,
                        color = if (isFuture) GrayMuted else Black,
                        textAlign = TextAlign.Center
                    )
                }
                
                cal.add(Calendar.DAY_OF_YEAR, 1) // Avanzar al siguiente día
            }
        }
    }
}
