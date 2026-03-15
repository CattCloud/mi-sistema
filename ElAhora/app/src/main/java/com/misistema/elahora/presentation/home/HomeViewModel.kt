package com.misistema.elahora.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.domain.model.Sistema
import com.misistema.elahora.domain.usecase.GetActiveSistemaUseCase
import com.misistema.elahora.domain.usecase.GetWeekLogsUseCase
import com.misistema.elahora.domain.usecase.ListSistemasUseCase
import com.misistema.elahora.domain.usecase.SaveDailyLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeSistema: Sistema? = null,
    val relatedSistemas: List<Sistema> = emptyList(), // Para el swipe si es necesario a futuro
    val todayLog: DailyLog? = null,
    val weekLogs: List<DailyLog> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getActiveSistema: GetActiveSistemaUseCase,
    private val listSistemas: ListSistemasUseCase, // Por si implementamos switch on swipe
    private val saveLog: SaveDailyLogUseCase,
    private val getWeekLogs: GetWeekLogsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = getActiveSistema()
            if (result.isSuccess) {
                val sistema = result.getOrNull()!!
                _state.update { it.copy(activeSistema = sistema, isLoading = false) }
                loadLogsForSystem(sistema.id)
            } else {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    private suspend fun loadLogsForSystem(systemId: String) {
        val todayStr = dateFormat.format(Date())
        
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val weekStartStr = dateFormat.format(cal.time)

        val logs = getWeekLogs(systemId, weekStartStr)
        val todayLog = logs.find { it.date == todayStr }

        _state.update { 
            it.copy(
                todayLog = todayLog,
                weekLogs = logs
            )
        }
    }

    fun onMarkDay(status: LogStatus, dateStr: String? = null, notes: String? = null) {
        val sistema = _state.value.activeSistema ?: return
        val targetDate = dateStr ?: dateFormat.format(Date())

        viewModelScope.launch {
            // Preservar notas anteriores si aplican
            val existingLog = _state.value.weekLogs.find { it.date == targetDate }
            
            val log = DailyLog(
                systemId = sistema.id,
                date = targetDate,
                status = status,
                notes = notes ?: existingLog?.notes
            )
            saveLog(log)
            loadLogsForSystem(sistema.id) // Refrescar UI
        }
    }

    fun onSaveNote(notes: String, dateStr: String? = null) {
        val sistema = _state.value.activeSistema ?: return
        val targetDate = dateStr ?: dateFormat.format(Date())

        viewModelScope.launch {
            val existingLog = _state.value.weekLogs.find { it.date == targetDate }
            val log = DailyLog(
                systemId = sistema.id,
                date = targetDate,
                status = existingLog?.status, // mantener estado actual
                notes = notes
            )
            saveLog(log)
            loadLogsForSystem(sistema.id)
        }
    }
}
