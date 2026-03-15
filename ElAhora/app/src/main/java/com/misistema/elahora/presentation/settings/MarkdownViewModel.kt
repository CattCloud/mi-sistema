package com.misistema.elahora.presentation.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misistema.elahora.domain.repository.GithubRepository
import com.misistema.elahora.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarkdownUiState(
    val isLoading: Boolean = true,
    val content: String? = null,
    val error: String? = null
)

@HiltViewModel
class MarkdownViewModel @Inject constructor(
    private val githubRepo: GithubRepository,
    private val localRepo: LocalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val fileName: String = checkNotNull(savedStateHandle["fileName"])

    private val _state = MutableStateFlow(MarkdownUiState())
    val state: StateFlow<MarkdownUiState> = _state.asStateFlow()

    init {
        loadMarkdown()
    }

    private fun loadMarkdown() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val token = localRepo.githubToken.firstOrNull()
            val userRepo = localRepo.githubRepo.firstOrNull() ?: "owner/repo"

            val parts = userRepo.split("/")
            if (parts.size != 2) {
                _state.update { it.copy(isLoading = false, error = "Repositorio mal configurado. Ve a Settings.") }
                return@launch
            }

            val downloadUrl = "https://raw.githubusercontent.com/${parts[0]}/${parts[1]}/main/$fileName"
            val result = githubRepo.downloadSistema(downloadUrl, token)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, content = result.getOrNull()) }
            } else {
                _state.update { 
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Error desconocido al descargar")
                }
            }
        }
    }
}
