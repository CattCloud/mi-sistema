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
}
