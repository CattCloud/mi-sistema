package com.misistema.elahora.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.domain.model.Sistema
import com.misistema.elahora.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(BgPage), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TextPrimary)
        }
        return
    }

    if (state.errorMessage != null || state.sistemas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(BgPage).padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.errorMessage ?: "Configura tus sistemas primero",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onNavigateToSettings) {
                    Text("Ir a Configuración")
                }
            }
        }
        return
    }

    val systems = state.sistemas
    val pagerState = rememberPagerState(pageCount = { systems.size })

    // Mover el pager a la página del sistema activo inicial
    LaunchedEffect(systems) {
        if (pagerState.currentPage == 0 && state.activeSistemaId.isNotEmpty()) {
            val idx = systems.indexOfFirst { it.id == state.activeSistemaId }
            if (idx > 0) {
                pagerState.scrollToPage(idx)
            }
        }
    }

    // Guardar el sistema activo al deslizar
    LaunchedEffect(pagerState.settledPage) {
        val newSystemId = systems[pagerState.settledPage].id
        viewModel.onSystemSwiped(newSystemId)
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize().background(BgPage)
    ) { page ->
        val sistema = systems[page]
        val theme = sistema.theme ?: DefaultSystemTheme
        val logs = state.weekLogsMap[sistema.id] ?: emptyList()
        val selectedLog = logs.find { it.date == state.selectedDate }

        ElAhoraTheme(systemTheme = theme) {
            SystemPageContent(
                sistema = sistema,
                state = state,
                selectedLog = selectedLog,
                totalSystems = systems.size,
                currentIndex = page,
                onNavigateToSettings = onNavigateToSettings,
                onSelectDate = { viewModel.onSelectDate(it) },
                onMarkDay = { status -> viewModel.onMarkDay(sistema.id, status) },
                onSaveNote = { note -> viewModel.onSaveNote(sistema.id, note) }
            )
        }
    }
}

@Composable
fun SystemPageContent(
    sistema: Sistema,
    state: HomeUiState,
    selectedLog: com.misistema.elahora.domain.model.DailyLog?,
    totalSystems: Int,
    currentIndex: Int,
    onNavigateToSettings: () -> Unit,
    onSelectDate: (String) -> Unit,
    onMarkDay: (LogStatus) -> Unit,
    onSaveNote: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Izquierda: Fecha y Quote
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val isToday = state.selectedDate == dateFormat.format(Date())
                val dayFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
                val dateObj = dateFormat.parse(state.selectedDate)
                val dayNameStr = dateObj?.let { dayFormat.format(it).replaceFirstChar { char -> char.uppercase() } } ?: ""
                val headerText = if (isToday) "HOY ($dayNameStr)" else dayNameStr.uppercase()
                
                Text(text = headerText, style = MaterialTheme.typography.labelSmall, color = LocalSystemTheme.current.accentMid)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${sistema.quote}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalSystemTheme.current.accentMid
                )
            }
            
            // Derecha: Iconos y Puntos
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Ícono del sistema + Engranaje en fila
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Ícono representativo del sistema
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .background(LocalSystemTheme.current.accentLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (sistema.icon) {
                            "moon" -> Icons.Rounded.NightsStay
                            "sun" -> Icons.Rounded.WbSunny
                            "fitness" -> Icons.Rounded.FitnessCenter
                            else -> Icons.Rounded.Settings
                        }
                        Icon(icon, contentDescription = null, tint = LocalSystemTheme.current.accentMain, modifier = Modifier.size(18.dp))
                    }

                    // Engranaje al lado
                    IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = LocalSystemTheme.current.accentMid, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dot Indicator centrado debajo
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 0 until totalSystems) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = if (i == currentIndex) LocalSystemTheme.current.accentMain else LocalSystemTheme.current.accentLight,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Card Identidad
        ElAhoraCard(
            containerColor = LocalSystemTheme.current.accentDark,
            contentColor = Color.White,
            elevation = 4.dp,
            shadowColor = LocalSystemTheme.current.accentMain.copy(alpha = 0.25f)
        ) {
            Text(text = "IDENTIDAD", style = MaterialTheme.typography.labelSmall, color = Color(0xFFB8A8E0))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = sistema.identidad, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, lineHeight = 20.sp))
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Acción Ideal
        ElAhoraCard {
            Text(text = "ACCIÓN IDEAL (EL DESTINO)", style = MaterialTheme.typography.labelSmall, color = LocalSystemTheme.current.accentMid)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = sistema.accionIdeal, style = MaterialTheme.typography.bodyLarge, lineHeight = 20.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Acción Diminuta Text
        Text(text = "ACCIÓN DIMINUTA HOY · FASE ${sistema.fases.actual}", style = MaterialTheme.typography.labelSmall, color = LocalSystemTheme.current.accentMid)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = sistema.accionDiminuta, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "+ ${sistema.miniRutina.nombre} (${sistema.miniRutina.duracionMin} min)", style = MaterialTheme.typography.bodyLarge, color = LocalSystemTheme.current.accentMain)
        
        Spacer(modifier = Modifier.height(14.dp))
        
        // Pasos
        ElAhoraCard {
            sistema.miniRutina.pasos.forEachIndexed { index, paso ->
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(LocalSystemTheme.current.accentLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "${index + 1}", style = MaterialTheme.typography.bodySmall, color = LocalSystemTheme.current.accentMain)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = paso, style = MaterialTheme.typography.bodyLarge)
                }
                if (index < sistema.miniRutina.pasos.size - 1) {
                    Divider(color = Color(0xFFF0EDF8), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Selector Días
        val dayLabels = listOf("L", "M", "Mi", "J", "V", "S", "D")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            state.weekDates.forEachIndexed { index, dateStr ->
                val logsForDate = state.weekLogsMap[sistema.id]?.find { it.date == dateStr }
                val isSelected = state.selectedDate == dateStr
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onSelectDate(dateStr) }
                ) {
                    Text(text = dayLabels.getOrElse(index) { "?" }, style = MaterialTheme.typography.labelSmall, color = LocalSystemTheme.current.accentMid)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val bgColor = when (logsForDate?.status) {
                        LogStatus.DONE -> StateDoneBg
                        LogStatus.NOT_DONE -> StateNotDoneBg
                        null -> LocalSystemTheme.current.accentLight
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(bgColor, CircleShape)
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) LocalSystemTheme.current.accentMain else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (logsForDate?.status == LogStatus.DONE) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = StateDoneText, modifier = Modifier.size(10.dp))
                        } else if (logsForDate?.status == LogStatus.NOT_DONE) {
                            Icon(Icons.Rounded.Close, contentDescription = null, tint = StateNotDoneText, modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Registro
        ElAhoraCard {
            Text(text = "Registro", style = MaterialTheme.typography.titleLarge.copy(fontSize = 13.sp), color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isDone = selectedLog?.status == LogStatus.DONE
                val isNotDone = selectedLog?.status == LogStatus.NOT_DONE
                
                Button(
                    onClick = { onMarkDay(LogStatus.DONE) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDone) StateDoneBg else Color(0xFFF0EDF8),
                        contentColor = if (isDone) StateDoneText else LocalSystemTheme.current.accentMain
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Cumplí", style = MaterialTheme.typography.bodyLarge, fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal)
                }
                
                Button(
                    onClick = { onMarkDay(LogStatus.NOT_DONE) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNotDone) StateNotDoneBg else Color(0xFFF0EDF8),
                        contentColor = if (isNotDone) StateNotDoneText else LocalSystemTheme.current.accentMain
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("No cumplí", style = MaterialTheme.typography.bodyLarge, fontWeight = if (isNotDone) FontWeight.Bold else FontWeight.Normal)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            var notesInput by remember { mutableStateOf(selectedLog?.notes ?: "") }
            var showError by remember { mutableStateOf(false) }
            
            LaunchedEffect(selectedLog) {
                notesInput = selectedLog?.notes ?: ""
                showError = false
            }

            ElAhoraInput(
                value = notesInput,
                onValueChange = { notesInput = it },
                placeholder = "Nota del día (opcional)"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ElAhoraButton(
                text = "Guardar",
                onClick = {
                    if (selectedLog?.status == null) {
                        showError = true
                    } else {
                        onSaveNote(notesInput)
                        showError = false
                    }
                }
            )
            if (showError) {
                Text(
                    text = "Selecciona Cumplí o No cumplí primero",
                    color = LocalSystemTheme.current.accentMain,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Peor Día
        ElAhoraCard(
            containerColor = AmberBg,
            contentColor = AmberBody,
            elevation = 2.dp,
            shadowColor = Color.Black.copy(alpha = 0.05f)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Rounded.Shield, contentDescription = null, tint = AmberIcon, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "PEOR DÍA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.06.sp), color = AmberText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = sistema.protocoloPeorDia, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp, lineHeight = 16.8.sp), color = AmberBody)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}
