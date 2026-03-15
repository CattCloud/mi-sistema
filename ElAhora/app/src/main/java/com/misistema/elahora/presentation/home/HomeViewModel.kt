package com.misistema.elahora.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.domain.model.Sistema
import com.misistema.elahora.domain.usecase.GetActiveSistemaUseCase
import com.misistema.elahora.domain.usecase.GetWeekLogsUseCase
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
    val selectedDate: String = "",
    val selectedLog: DailyLog? = null,
    val weekDates: List<String> = emptyList(),
    val weekLogs: List<DailyLog> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getActiveSistema: GetActiveSistemaUseCase,
    private val saveLog: SaveDailyLogUseCase,
    private val getWeekLogs: GetWeekLogsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        val todayStr = dateFormat.format(Date())
        val weekDates = getCurrentWeekDates()
        _state.update { it.copy(selectedDate = todayStr, weekDates = weekDates) }
        loadData()
    }
    
    private fun getCurrentWeekDates(): List<String> {
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val dates = mutableListOf<String>()
        for (i in 0..6) {
            dates.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
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
                        errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido. Asegúrate de configurar la app primero."
                    )
                }
            }
        }
    }

    private suspend fun loadLogsForSystem(systemId: String) {
        val weekStartStr = _state.value.weekDates.first()
        val logs = getWeekLogs(systemId, weekStartStr)
        
        val selectedLog = logs.find { it.date == _state.value.selectedDate }

        _state.update { 
            it.copy(
                selectedLog = selectedLog,
                weekLogs = logs
            )
        }
    }
    
    fun onSelectDate(date: String) {
        _state.update { 
            val log = it.weekLogs.find { l -> l.date == date }
            it.copy(selectedDate = date, selectedLog = log)
        }
    }

    fun onMarkDay(status: LogStatus) {
        val sistema = _state.value.activeSistema ?: return
        val targetDate = _state.value.selectedDate

        viewModelScope.launch {
            val existingLog = _state.value.weekLogs.find { it.date == targetDate }
            
            val log = DailyLog(
                systemId = sistema.id,
                date = targetDate,
                status = status,
                notes = existingLog?.notes
            )
            saveLog(log)
            loadLogsForSystem(sistema.id)
        }
    }

    fun onSaveNote(notes: String) {
        val sistema = _state.value.activeSistema ?: return
        val targetDate = _state.value.selectedDate

        viewModelScope.launch {
            val existingLog = _state.value.weekLogs.find { it.date == targetDate }
            val log = DailyLog(
                systemId = sistema.id,
                date = targetDate,
                status = existingLog?.status, 
                notes = notes
            )
            saveLog(log)
            loadLogsForSystem(sistema.id)
        }
    }
}
