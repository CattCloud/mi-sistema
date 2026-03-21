package com.misistema.elahora.domain.repository

import com.misistema.elahora.domain.model.DailyLog
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    val activeSystemId: Flow<String?>
    val githubToken: Flow<String?>
    val githubRepo: Flow<String?>
    
    suspend fun saveActiveSystemId(id: String)
    suspend fun saveToken(token: String)
    suspend fun saveRepo(repo: String)

    suspend fun saveDailyLog(log: DailyLog)
    suspend fun getDailyLog(systemId: String, date: String): DailyLog?
    suspend fun getWeekLogs(systemId: String, weekStart: String): List<DailyLog>
    suspend fun getAllLogsForSystem(systemId: String): List<DailyLog>
    
    suspend fun getLocalSystemJson(systemId: String): String?
    suspend fun listAvailableSystems(): List<String>
    
    // Cache de sistemas descargados de GitHub
    suspend fun saveCachedSystemJson(systemId: String, jsonContent: String)
    suspend fun getCachedSystemJson(systemId: String): String?
    suspend fun listCachedSystems(): List<String>
}
