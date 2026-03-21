package com.misistema.elahora.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.domain.model.Sistema
import com.misistema.elahora.domain.model.SistemaParser
import com.misistema.elahora.domain.repository.LocalRepository
import com.misistema.elahora.domain.usecase.GetWeekLogsUseCase
import com.misistema.elahora.domain.usecase.SaveDailyLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val sistemas: List<Sistema> = emptyList(),
    val activeSistemaId: String = "",
    val selectedDate: String = "",
    val weekDates: List<String> = emptyList(),
    val weekLogsMap: Map<String, List<DailyLog>> = emptyMap(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val localRepo: LocalRepository,
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
            
            try {
                val activeId = localRepo.activeSystemId.first() ?: ""
                _state.update { it.copy(activeSistemaId = activeId) }

                val allIds = localRepo.listAvailableSystems()
                val cachedIds = localRepo.listCachedSystems()
                val combined = (allIds + cachedIds).distinct()
                
                val sistemasLoaded = combined.mapNotNull { id ->
                    val json = localRepo.getCachedSystemJson(id) ?: localRepo.getLocalSystemJson(id)
                    if (json != null) {
                        try { SistemaParser.parseJson(json) } catch (e: Exception) { null }
                    } else null
                }
                
                if (sistemasLoaded.isEmpty()) {
                    _state.update { it.copy(isLoading = false, errorMessage = "No hay sistemas disponibles") }
                    return@launch
                }

                _state.update { it.copy(sistemas = sistemasLoaded, isLoading = false) }
                
                sistemasLoaded.forEach { sistema ->
                    loadLogsForSystem(sistema.id)
                }
            } catch (e: Exception) {
                 _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error desconocido") }
            }
        }
    }

    private suspend fun loadLogsForSystem(systemId: String) {
        val weekStartStr = _state.value.weekDates.first()
        val logs = getWeekLogs(systemId, weekStartStr)
        
        _state.update { 
            val newLogsMap = it.weekLogsMap.toMutableMap()
            newLogsMap[systemId] = logs
            it.copy(weekLogsMap = newLogsMap)
        }
    }

    fun onSystemSwiped(newSystemId: String) {
        if (_state.value.activeSistemaId != newSystemId) {
            _state.update { it.copy(activeSistemaId = newSystemId) }
            viewModelScope.launch {
                localRepo.saveActiveSystemId(newSystemId)
            }
        }
    }
    
    fun onSelectDate(date: String) {
        _state.update { it.copy(selectedDate = date) }
    }

    fun onMarkDay(systemId: String, status: LogStatus) {
        val targetDate = _state.value.selectedDate

        viewModelScope.launch {
            val logs = _state.value.weekLogsMap[systemId] ?: emptyList()
            val existingLog = logs.find { it.date == targetDate }
            
            val log = DailyLog(
                systemId = systemId,
                date = targetDate,
                status = status,
                notes = existingLog?.notes
            )
            saveLog(log)
            loadLogsForSystem(systemId)
        }
    }

    fun onSaveNote(systemId: String, notes: String) {
        val targetDate = _state.value.selectedDate

        viewModelScope.launch {
            val logs = _state.value.weekLogsMap[systemId] ?: emptyList()
            val existingLog = logs.find { it.date == targetDate }
            val log = DailyLog(
                systemId = systemId,
                date = targetDate,
                status = existingLog?.status, 
                notes = notes
            )
            saveLog(log)
            loadLogsForSystem(systemId)
        }
    }
}
