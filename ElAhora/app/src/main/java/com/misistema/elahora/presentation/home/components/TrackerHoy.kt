package com.misistema.elahora.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.presentation.theme.*

@Composable
fun TrackerHoy(
    todayLog: DailyLog?,
    onMarkDay: (LogStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "RASTREO DE HOY", style = Typography.labelSmall)
        Spacer(modifier = Modifier.height(12.dp))

        if (todayLog?.status != null) {
            Text(
                text = if (todayLog.status == LogStatus.DONE) "¡Día cumplido! 🎉" else "Día no cumplido.",
                style = Typography.bodyLarge
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HenryButton(
                    text = "NO CUMPLÍ",
                    isActive = false,
                    onClick = { onMarkDay(LogStatus.NOT_DONE) },
                    modifier = Modifier.weight(1f)
                )
                HenryButton(
                    text = "CUMPLÍ",
                    isActive = true,
                    onClick = { onMarkDay(LogStatus.DONE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
