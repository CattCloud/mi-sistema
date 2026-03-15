package com.misistema.elahora.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misistema.elahora.presentation.theme.Black
import com.misistema.elahora.presentation.theme.Cyan
import com.misistema.elahora.presentation.theme.NeuCard
import com.misistema.elahora.presentation.theme.Typography
import com.misistema.elahora.presentation.theme.Violet
import com.misistema.elahora.presentation.theme.White
import com.misistema.elahora.presentation.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToMarkdown: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CONFIGURACIÓN", style = Typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // GitHub Sync Section
            NeuCard(backgroundColor = Cyan, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SINCRONIZACIÓN GITHUB", style = Typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Repositorio (owner/repo)", style = Typography.labelSmall)
                    TextField(
                        value = state.githubRepo,
                        onValueChange = viewModel::onRepoChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = White,
                            focusedContainerColor = White,
                            unfocusedIndicatorColor = Black,
                            focusedIndicatorColor = Black,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Token de Acceso", style = Typography.labelSmall)
                    TextField(
                        value = state.githubToken,
                        onValueChange = viewModel::onTokenChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = White,
                            focusedContainerColor = White,
                            unfocusedIndicatorColor = Black,
                            focusedIndicatorColor = Black,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sistema Activo manually
            NeuCard(backgroundColor = Yellow, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SISTEMA ACTIVO", style = Typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = state.activeSystemId,
                        onValueChange = viewModel::onActiveSystemChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = White,
                            focusedContainerColor = White,
                            unfocusedIndicatorColor = Black,
                            focusedIndicatorColor = Black,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Docs System
            NeuCard(backgroundColor = Violet, contentColor = White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("DOCUMENTOS COMPLEMENTARIOS", style = Typography.labelSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // En lugar de componentes complejos, por ahora crearemos NeuButtons para navegar 
                    com.misistema.elahora.presentation.theme.NeuButton(
                        text = "LEER YO.MD",
                        backgroundColor = White,
                        contentColor = Black,
                        onClick = { onNavigateToMarkdown("yo.md") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    com.misistema.elahora.presentation.theme.NeuButton(
                        text = "LEER REGLAS DEL SISTEMA",
                        backgroundColor = White,
                        contentColor = Black,
                        onClick = { onNavigateToMarkdown("reglas-del-sistema.md") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
