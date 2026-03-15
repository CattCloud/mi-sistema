package com.misistema.elahora.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misistema.elahora.presentation.home.components.AccionCard
import com.misistema.elahora.presentation.home.components.DiasAnteriores
import com.misistema.elahora.presentation.home.components.MantraCard
import com.misistema.elahora.presentation.home.components.ProgresoFases
import com.misistema.elahora.presentation.home.components.TrackerHoy
import com.misistema.elahora.presentation.theme.Black
import com.misistema.elahora.presentation.theme.Cyan
import com.misistema.elahora.presentation.theme.NeuButton
import com.misistema.elahora.presentation.theme.Typography
import com.misistema.elahora.presentation.theme.White

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
                title = {
                    Text(
                        text = state.activeSistema?.nombre?.uppercase() ?: "EL AHORA",
                        style = Typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Black
                )
            )
        },
        containerColor = White
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Black)
            }
        } else if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.errorMessage!!, 
                        style = Typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    NeuButton(
                        text = "Configurar Sistema",
                        backgroundColor = Cyan,
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
                
                ProgresoFases(
                    faseActual = sistema.faseActual,
                    totalFases = sistema.totalFases
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                MantraCard(mantra = sistema.mantra)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AccionCard(
                    accionDiminuta = sistema.accionDiminuta,
                    pasos = sistema.pasos
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TrackerHoy(
                    todayLog = state.todayLog,
                    onMarkDay = { status -> viewModel.onMarkDay(status) }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                DiasAnteriores(
                    weekLogs = state.weekLogs,
                    onDayClick = { dateStr -> 
                        // Implementación futura: Popup para marcar retrospectivo
                    }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
