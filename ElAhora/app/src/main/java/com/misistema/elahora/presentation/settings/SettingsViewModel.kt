package com.misistema.elahora.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misistema.elahora.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val githubToken: String = "",
    val githubRepo: String = "owner/repo",
    val activeSystemId: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localRepo: LocalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            localRepo.githubToken.collect { token ->
                _state.update { it.copy(githubToken = token ?: "") }
            }
        }
        viewModelScope.launch {
            localRepo.githubRepo.collect { repo ->
                _state.update { it.copy(githubRepo = repo ?: "owner/repo") }
            }
        }
        viewModelScope.launch {
            localRepo.activeSystemId.collect { id ->
                _state.update { it.copy(activeSystemId = id ?: "") }
            }
        }
    }

    fun onTokenChange(newToken: String) {
        viewModelScope.launch { localRepo.saveToken(newToken) }
    }

    fun onRepoChange(newRepo: String) {
        viewModelScope.launch { localRepo.saveRepo(newRepo) }
    }

    fun onActiveSystemChange(newId: String) {
        viewModelScope.launch { localRepo.saveActiveSystemId(newId) }
    }
}
