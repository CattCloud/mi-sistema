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
import com.misistema.elahora.presentation.theme.*

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
                title = { Text("CONFIGURACIÓN", style = Typography.labelSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPage)
            )
        },
        containerColor = BgPage
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
            HenryCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("SINCRONIZACIÓN GITHUB", style = Typography.labelSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Repositorio (owner/repo)", style = Typography.bodyLarge, color = TextSecondary)
                    TextField(
                        value = state.githubRepo,
                        onValueChange = viewModel::onRepoChange,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textStyle = Typography.bodyLarge,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = AccentLight,
                            focusedContainerColor = BgCard,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = DividerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Token de Acceso", style = Typography.bodyLarge, color = TextSecondary)
                    TextField(
                        value = state.githubToken,
                        onValueChange = viewModel::onTokenChange,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textStyle = Typography.bodyLarge,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = AccentLight,
                            focusedContainerColor = BgCard,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = DividerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sistema Activo manually
            HenryCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("SISTEMA ACTIVO", style = Typography.labelSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nombre del archivo JSON (ej: 01-calidad-sueno)", style = Typography.bodyLarge, color = TextSecondary)
                    TextField(
                        value = state.activeSystemId,
                        onValueChange = viewModel::onActiveSystemChange,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textStyle = Typography.bodyLarge,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = AccentLight,
                            focusedContainerColor = BgCard,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = DividerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Docs System
            HenryCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("DOCUMENTOS COMPLEMENTARIOS", style = Typography.labelSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HenryButton(
                        text = "LEER YO.MD",
                        onClick = { onNavigateToMarkdown("yo.md") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HenryButton(
                        text = "LEER REGLAS DEL SISTEMA",
                        onClick = { onNavigateToMarkdown("reglas-del-sistema.md") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
