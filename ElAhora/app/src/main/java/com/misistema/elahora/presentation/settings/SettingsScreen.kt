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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misistema.elahora.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToMarkdown: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CONFIGURACIÓN", style = MaterialTheme.typography.labelSmall) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextPrimary
                        )
                    }
                },
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
            ElAhoraCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("SINCRONIZACIÓN GITHUB", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Repositorio (owner/repo)", style = MaterialTheme.typography.bodyLarge, color = LocalSystemTheme.current.accentMid)
                    TextField(
                        value = state.inputRepo,
                        onValueChange = viewModel::onInputRepoChange,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = LocalSystemTheme.current.accentLight,
                            focusedContainerColor = CardSurface,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = LocalSystemTheme.current.accentMain,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        placeholder = { Text("ej: mi-usuario/mi-sistema", color = LocalSystemTheme.current.accentMid.copy(alpha = 0.5f)) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Token de Acceso Github (opcional)", style = MaterialTheme.typography.bodyLarge, color = LocalSystemTheme.current.accentMid)
                    TextField(
                        value = state.inputToken,
                        onValueChange = viewModel::onInputTokenChange,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = LocalSystemTheme.current.accentLight,
                            focusedContainerColor = CardSurface,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = LocalSystemTheme.current.accentMain,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        placeholder = { Text("ghp_... (requerido para exportar)", color = LocalSystemTheme.current.accentMid.copy(alpha = 0.5f)) }
                    )
                    
                    // Mensaje de aviso sobre token justo debajo del campo
                    if (state.githubRepo.isNotEmpty() && state.githubToken.isEmpty()) {
                        Text(
                            text = "Sin token solo puedes leer repos públicos. Para exportar registros, agrega un token.",
                            color = LocalSystemTheme.current.accentMid,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    ElAhoraButton(
                        text = if (state.syncStatus == SyncStatus.LOADING) "CONECTANDO..." else "CONECTAR",
                        isActive = state.syncStatus != SyncStatus.LOADING,
                        onClick = {
                            if (state.syncStatus != SyncStatus.LOADING) viewModel.onConnect()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Botón ACTUALIZAR (solo repo requerido)
                    if (state.githubRepo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ElAhoraButton(
                            text = if (state.syncStatus == SyncStatus.LOADING) "DESCARGANDO SISTEMAS..." else "ACTUALIZAR SISTEMAS DESDE GITHUB",
                            isActive = state.syncStatus != SyncStatus.LOADING,
                            onClick = {
                                if (state.syncStatus != SyncStatus.LOADING) viewModel.onSyncSystems()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Botón EXPORTAR (requiere token)
                    if (state.githubRepo.isNotEmpty() && state.githubToken.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ElAhoraButton(
                            text = if (state.exportStatus == ExportStatus.LOADING) "EXPORTANDO..." else "EXPORTAR REGISTROS A GITHUB",
                            isActive = state.exportStatus != ExportStatus.LOADING,
                            onClick = {
                                if (state.exportStatus != ExportStatus.LOADING) viewModel.onExportLogs()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (state.exportStatus == ExportStatus.SUCCESS) {
                            Text("Registros exportados.", color = LocalSystemTheme.current.accentMid, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
                        } else if (state.exportStatus == ExportStatus.ERROR) {
                            Text(state.exportErrorMessage ?: "Error", color = Color(0xFFC65555), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
                        }
                    }

                    // Mensaje de estado (conexión)
                    if (state.syncStatus == SyncStatus.SUCCESS) {
                        Text(
                            text = "Conexión exitosa.",
                            color = LocalSystemTheme.current.accentMid,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    } else if (state.syncStatus == SyncStatus.ERROR) {
                        Text(
                            text = state.syncErrorMessage ?: "Error",
                            color = Color(0xFFC65555),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sistema Activo Selection
            ElAhoraCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("SISTEMA ACTIVO", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Elige cuál sistema quieres seguir hoy", style = MaterialTheme.typography.bodyLarge, color = LocalSystemTheme.current.accentMid)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        TextField(
                            value = state.activeSystemId.ifEmpty { "vacio" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = LocalSystemTheme.current.accentLight,
                                focusedContainerColor = CardSurface,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = LocalSystemTheme.current.accentMain,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            state.availableSystems.forEach { systemName ->
                                DropdownMenuItem(
                                    text = { Text(systemName, style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        viewModel.onActiveSystemChange(systemName)
                                        expanded = false
                                    }
                                )
                            }
                            if (state.availableSystems.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No se encontraron sistemas locales", style = MaterialTheme.typography.bodyLarge, color = LocalSystemTheme.current.accentMid) },
                                    onClick = { expanded = false }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Docs System
            ElAhoraCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("DOCUMENTOS COMPLEMENTARIOS", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ElAhoraButton(
                        text = "LEER YO.MD",
                        onClick = { onNavigateToMarkdown("yo.md") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ElAhoraButton(
                        text = "LEER REGLAS DEL SISTEMA",
                        onClick = { onNavigateToMarkdown("reglas-del-sistema.md") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
