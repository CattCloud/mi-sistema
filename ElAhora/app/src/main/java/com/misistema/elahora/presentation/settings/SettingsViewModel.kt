package com.misistema.elahora.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misistema.elahora.domain.repository.GithubRepository
import com.misistema.elahora.domain.repository.LocalRepository
import com.misistema.elahora.domain.usecase.ExportLogsToGithubUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SyncStatus {
    IDLE, LOADING, SUCCESS, ERROR
}

enum class ExportStatus {
    IDLE, LOADING, SUCCESS, ERROR
}

data class SettingsUiState(
    val githubToken: String = "",
    val githubRepo: String = "",
    val inputToken: String = "",
    val inputRepo: String = "",
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val syncErrorMessage: String? = null,
    val exportStatus: ExportStatus = ExportStatus.IDLE,
    val exportErrorMessage: String? = null,
    val activeSystemId: String = "",
    val availableSystems: List<String> = emptyList()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localRepo: LocalRepository,
    private val githubRepoRef: GithubRepository,
    private val exportLogsUseCase: ExportLogsToGithubUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        loadSystems()
        viewModelScope.launch {
            localRepo.githubToken.collect { token ->
                val safeToken = token ?: ""
                _state.update { oldState -> 
                    oldState.copy(
                        githubToken = safeToken,
                        inputToken = if (oldState.inputToken.isEmpty()) safeToken else oldState.inputToken
                    ) 
                }
            }
        }
        viewModelScope.launch {
            localRepo.githubRepo.collect { repo ->
                val safeRepo = repo ?: ""
                _state.update { oldState -> 
                    oldState.copy(
                        githubRepo = safeRepo,
                        inputRepo = if (oldState.inputRepo.isEmpty()) safeRepo else oldState.inputRepo
                    ) 
                }
            }
        }
        viewModelScope.launch {
            localRepo.activeSystemId.collect { id ->
                _state.update { it.copy(activeSystemId = id ?: "") }
            }
        }
    }

    fun onInputTokenChange(newToken: String) {
        _state.update { it.copy(inputToken = newToken, syncStatus = SyncStatus.IDLE) }
    }

    fun onInputRepoChange(newRepo: String) {
        _state.update { it.copy(inputRepo = newRepo, syncStatus = SyncStatus.IDLE) }
    }

    fun onActiveSystemChange(newId: String) {
        viewModelScope.launch { localRepo.saveActiveSystemId(newId) }
    }

    /**
     * Carga la lista de sistemas unificada:
     * - Local assets (archivos de fábrica del APK)
     * - Caché local (sistemas descargados previamente de GitHub)
     * Los combina sin duplicados.
     */
    private fun loadSystems() {
        viewModelScope.launch {
            val assetSystems = localRepo.listAvailableSystems()
            val cachedSystems = localRepo.listCachedSystems()
            val unified = (assetSystems + cachedSystems).distinct().sorted()
            _state.update { it.copy(availableSystems = unified) }
        }
    }

    /**
     * Sincroniza con GitHub: descarga la lista de sistemas disponibles,
     * los guarda en caché y actualiza el selector.
     */
    fun onSyncSystems() {
        val token = _state.value.githubToken.takeIf { it.isNotEmpty() }
            ?: _state.value.inputToken.trim().takeIf { it.isNotEmpty() }
        // Usar el repo guardado, o si no, el que está escrito en el input
        val repo = _state.value.githubRepo.ifEmpty { _state.value.inputRepo.trim() }
        if (repo.isEmpty()) {
            _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = "Ingresa el repositorio primero.") }
            return
        }

        val parts = repo.split("/")
        if (parts.size != 2) {
            _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = "Formato inválido. Usa owner/repo") }
            return
        }

        _state.update { it.copy(syncStatus = SyncStatus.LOADING, syncErrorMessage = null) }

        viewModelScope.launch {
            val listResult = githubRepoRef.listSistemas(parts[0], parts[1], token)
            if (listResult.isSuccess) {
                val files = listResult.getOrNull() ?: emptyList()
                val jsonFiles = files.filter { it.name.endsWith(".json") }

                if (jsonFiles.isEmpty()) {
                    _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = "No se encontraron archivos .json en la carpeta 'sistemas/' del repo.") }
                    return@launch
                }

                // Descargar cada sistema y guardarlo en caché
                jsonFiles.forEach { file ->
                    val downloadResult = githubRepoRef.downloadSistema(file.downloadUrl, token)
                    if (downloadResult.isSuccess) {
                        val systemId = file.name.removeSuffix(".json")
                        localRepo.saveCachedSystemJson(systemId, downloadResult.getOrNull()!!)
                    }
                }

                // Actualizar el listado del selector con los nuevos sistemas
                loadSystems()
                _state.update { it.copy(syncStatus = SyncStatus.SUCCESS) }
            } else {
                val errorMsg = listResult.exceptionOrNull()?.message ?: "Error desconocido"
                _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = "Error GitHub: $errorMsg") }
            }
        }
    }
    fun onConnect() {
        val token = _state.value.inputToken.trim()
        val repo = _state.value.inputRepo.trim()

        if (repo.isEmpty()) {
            _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = "Completa el repositorio.") }
            return
        }

        val parts = repo.split("/")
        if (parts.size != 2) {
            _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = "Formato inválido. Usa owner/repo") }
            return
        }

        _state.update { it.copy(syncStatus = SyncStatus.LOADING, syncErrorMessage = null) }

        viewModelScope.launch {
            try {
                val result = githubRepoRef.listSistemas(parts[0], parts[1], token)
                if (result.isSuccess) {
                    localRepo.saveToken(token)
                    localRepo.saveRepo(repo)
                    _state.update { 
                        it.copy(
                            syncStatus = SyncStatus.SUCCESS, 
                            githubToken = token, 
                            githubRepo = repo 
                        ) 
                    }
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                    val finalMsg = when {
                        errorMsg.contains("401") -> "Error: Token inválido o expirado."
                        errorMsg.contains("404") -> "Error: Repositorio no encontrado (¿es privado?)."
                        else -> "Error de conexión a GitHub."
                    }
                    _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = finalMsg) }
                }
            } catch(e: Exception) {
                _state.update { it.copy(syncStatus = SyncStatus.ERROR, syncErrorMessage = "Error inesperado de red.") }
            }
        }
    }

    fun onExportLogs() {
        _state.update { it.copy(exportStatus = ExportStatus.LOADING, exportErrorMessage = null) }
        viewModelScope.launch {
            val result = exportLogsUseCase()
            if (result.isSuccess) {
                _state.update { it.copy(exportStatus = ExportStatus.SUCCESS) }
            } else {
                _state.update { it.copy(exportStatus = ExportStatus.ERROR, exportErrorMessage = result.exceptionOrNull()?.message ?: "Error desconocido") }
            }
        }
    }
}
