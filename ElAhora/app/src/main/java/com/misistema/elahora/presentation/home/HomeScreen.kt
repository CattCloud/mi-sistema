package com.misistema.elahora.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // Sin título visible
                actions = {
                    val icon = when (state.activeSistema?.icon) {
                        "moon" -> Icons.Rounded.NightsStay
                        "sun" -> Icons.Rounded.WbSunny
                        else -> Icons.Rounded.Settings
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = icon, 
                            contentDescription = "Configuración",
                            tint = IconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgPage,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BgPage // Fondo arena/beige
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextPrimary)
            }
        } else if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.errorMessage!!, 
                        style = Typography.bodyLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    HenryButton(
                        text = "Configurar Sistema",
                        onClick = onNavigateToSettings
                    )
                }
            }
        } else if (state.activeSistema != null) {
            val sistema = state.activeSistema!!
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quote Centrada
                Text(
                    text = "\"${sistema.quote}\"",
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Identidad
                HenryCard {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "IDENTIDAD", style = Typography.labelSmall)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = sistema.identidad, style = Typography.bodyLarge)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Acción Ideal
                HenryCard {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "ACCION IDEAL (EL DESTINO)", style = Typography.labelSmall)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = sistema.accionIdeal, style = Typography.bodyLarge)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Acción Diminuta Text (Not a card, part of the layout flow)
                Text(text = "ACCION DIMINUTA HOY", style = Typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sistema.accionDiminuta, 
                    style = Typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "+ ${sistema.miniRutina.nombre} (${sistema.miniRutina.duracionMin} min)", 
                    style = Typography.bodyLarge,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Pasos de Mini Rutina (Card)
                HenryCard {
                    Column(modifier = Modifier.padding(20.dp)) {
                        sistema.miniRutina.pasos.forEachIndexed { index, paso ->
                            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                // Círculo sutil para el número
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                                    color = Color.Transparent,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "${index + 1}", style = Typography.labelSmall, color = TextSecondary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = paso, style = Typography.bodyLarge)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Panel HOY y Tracker
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val isToday = state.selectedDate == dateFormat.format(Date())
                val dayFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
                val dateObj = dateFormat.parse(state.selectedDate)
                val dayNameStr = dateObj?.let { dayFormat.format(it).replaceFirstChar { char -> char.uppercase() } } ?: ""

                val headerText = if (isToday) "HOY ($dayNameStr)" else dayNameStr.uppercase()

                HenryCard {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = headerText, style = Typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Botones Cumpli / No Cumpli
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HenryButton(
                                text = "CUMPLI",
                                isActive = state.selectedLog?.status == LogStatus.DONE,
                                onClick = { viewModel.onMarkDay(LogStatus.DONE) },
                                modifier = Modifier.weight(1f)
                            )
                            HenryButton(
                                text = "NO CUMPLI",
                                isActive = state.selectedLog?.status == LogStatus.NOT_DONE,
                                onClick = { viewModel.onMarkDay(LogStatus.NOT_DONE) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // TextField Nota
                        var notesInput by remember { mutableStateOf(state.selectedLog?.notes ?: "") }
                        
                        // Sincronizar input con el estado externo cuando cambia el día
                        LaunchedEffect(state.selectedLog) {
                            notesInput = state.selectedLog?.notes ?: ""
                        }

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { 
                                notesInput = it
                                viewModel.onSaveNote(it)
                            },
                            placeholder = { Text("Nota del día", style = Typography.bodyLarge) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = Typography.bodyLarge,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DividerColor,
                                unfocusedBorderColor = DividerColor,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            singleLine = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Días de la semana Selector
                val labels = listOf("L", "M", "Mi", "J", "V", "S", "D")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    state.weekDates.forEachIndexed { index, dateStr ->
                        HenryDayTag(
                            dayName = labels.getOrElse(index) { "?" },
                            isSelected = state.selectedDate == dateStr,
                            onClick = { viewModel.onSelectDate(dateStr) },
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
