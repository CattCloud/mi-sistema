package com.misistema.elahora.data.repository

import com.misistema.elahora.data.local.datastore.SistemaPreferences
import com.misistema.elahora.data.local.db.DailyLogDao
import com.misistema.elahora.data.local.db.DailyLogEntity
import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.model.LogStatus
import com.misistema.elahora.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import android.content.Context
import java.io.File
import java.io.IOException

class LocalRepositoryImpl(
    private val context: Context,
    private val dao: DailyLogDao,
    private val prefs: SistemaPreferences
) : LocalRepository {

    // Directorio de caché para sistemas descargados de GitHub
    private val cacheDir: File
        get() = File(context.filesDir, "sistemas_cache").also { it.mkdirs() }

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

    override suspend fun getAllLogsForSystem(systemId: String): List<DailyLog> {
        return dao.getAllLogsForSystem(systemId).map { entity ->
            DailyLog(
                systemId = entity.systemId,
                date = entity.date,
                status = entity.status?.let { LogStatus.valueOf(it) },
                notes = entity.notes
            )
        }
    }

    // Lee desde assets (archivos de fábrica incluidos en el APK)
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

    // Lista los sistemas de los archivos de fábrica (assets)
    override suspend fun listAvailableSystems(): List<String> {
        return try {
            context.assets.list("sistemas")
                ?.filter { it.endsWith(".json") }
                ?.map { it.removeSuffix(".json") }
                ?: emptyList()
        } catch (e: IOException) {
            emptyList()
        }
    }

    // Guarda un JSON descargado de GitHub en el directorio privado de la app
    override suspend fun saveCachedSystemJson(systemId: String, jsonContent: String) {
        val file = File(cacheDir, "$systemId.json")
        file.writeText(jsonContent, Charsets.UTF_8)
    }

    // Lee un sistema desde la caché local (descargado previamente de GitHub)
    override suspend fun getCachedSystemJson(systemId: String): String? {
        return try {
            val file = File(cacheDir, "$systemId.json")
            if (file.exists()) file.readText(Charsets.UTF_8) else null
        } catch (e: IOException) {
            null
        }
    }

    // Lista todos los sistemas en caché (descargados de GitHub)
    override suspend fun listCachedSystems(): List<String> {
        return try {
            cacheDir.listFiles()
                ?.filter { it.extension == "json" }
                ?.map { it.nameWithoutExtension }
                ?: emptyList()
        } catch (e: IOException) {
            emptyList()
        }
    }
}
