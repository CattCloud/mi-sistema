package com.misistema.elahora.data.repository

import com.misistema.elahora.data.local.datastore.SistemaPreferences
import com.misistema.elahora.data.local.db.DailyLogDao
import com.misistema.elahora.data.local.db.DailyLogEntity
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import android.content.Context
import java.io.IOException

class LocalRepositoryImpl(
    private val context: Context,
    private val dao: DailyLogDao,
    private val prefs: SistemaPreferences
) : LocalRepository {

    override val activeSystemId: Flow<String?> = prefs.activeSystemId
    override val githubToken: Flow<String?> = prefs.githubToken
    override val githubRepo: Flow<String?> = prefs.githubRepo

    override suspend fun saveActiveSystemId(id: String) = prefs.saveActiveSystemId(id)
    override suspend fun saveToken(token: String) = prefs.saveGithubToken(token)
    override suspend fun saveRepo(repo: String) = prefs.saveGithubRepo(repo)

    override suspend fun saveDailyLog(log: DailyLog) {
        val entity = DailyLogEntity(
            systemId = log.systemId,
            date = log.date,
            status = log.status?.name,
            notes = log.notes
        )
        // Check si existe para no duplicar inserts y solo updatear el status
        val existing = dao.getByDate(log.systemId, log.date)
        if (existing != null) {
            dao.insertOrUpdate(entity.copy(id = existing.id))
        } else {
            dao.insertOrUpdate(entity)
        }
    }

    override suspend fun getDailyLog(systemId: String, date: String): DailyLog? {
        val entity = dao.getByDate(systemId, date) ?: return null
        return DailyLog(
            systemId = entity.systemId,
            date = entity.date,
            status = entity.status?.let { LogStatus.valueOf(it) },
            notes = entity.notes
        )
    }

    override suspend fun getWeekLogs(systemId: String, weekStart: String): List<DailyLog> {
        return dao.getLogsForWeek(systemId, weekStart).map { entity ->
            DailyLog(
                systemId = entity.systemId,
                date = entity.date,
                status = entity.status?.let { LogStatus.valueOf(it) },
                notes = entity.notes
            )
        }
    }

    override suspend fun getLocalSystemJson(systemId: String): String? {
        return try {
            val inputStream = context.assets.open("sistemas/$systemId.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            null
        }
    }
}
