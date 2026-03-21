package com.misistema.elahora.domain.usecase

import android.util.Base64
import com.misistema.elahora.domain.repository.GithubRepository
import com.misistema.elahora.domain.repository.LocalRepository
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class ExportLogsToGithubUseCase(
    private val githubRepo: GithubRepository,
    private val localRepo: LocalRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val activeId = localRepo.activeSystemId.firstOrNull()
            ?: return Result.failure(Exception("No hay sistema activo configurado"))

        val token = localRepo.githubToken.firstOrNull()
        val userRepo = localRepo.githubRepo.firstOrNull()
            ?: return Result.failure(Exception("Repositorio Github no configurado"))

        val parts = userRepo.split("/")
        if (parts.size != 2) return Result.failure(Exception("Repo inválido"))
        val owner = parts[0]
        val repoName = parts[1]

        val allLogs = localRepo.getAllLogsForSystem(activeId)
        if (allLogs.isEmpty()) {
            return Result.failure(Exception("No hay registros para exportar en este sistema."))
        }

        // Agrupar logs por mes (yyyy-MM)
        val logsByMonth = allLogs.groupBy { it.date.substring(0, 7) }

        var hasError = false
        var lastError: Throwable? = null

        // Subir un archivo JSON por mes
        for ((month, logs) in logsByMonth) {
            try {
                // Construir el JSON
                val rootObj = JSONObject()
                rootObj.put("sistema", activeId)
                rootObj.put("mes", month)

                val logsArray = JSONArray()
                logs.forEach { log ->
                    if (log.status != null) {
                        val logObj = JSONObject()
                        logObj.put("fecha", log.date)
                        logObj.put("estado", log.status.name)
                        logObj.put("nota", log.notes)
                        logsArray.put(logObj)
                    }
                }
                rootObj.put("registros", logsArray)

                val jsonString = rootObj.toString(2)
                
                // Codificar a Base64 sin saltos de línea (NO_WRAP es crítico para la API de GitHub)
                val base64Content = Base64.encodeToString(jsonString.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                val path = "registros/$activeId/$month.json"
                val commitMessage = "Export logs para $activeId ($month)"

                val result = githubRepo.saveFile(owner, repoName, path, base64Content, commitMessage, token)
                if (result.isFailure) {
                    hasError = true
                    lastError = result.exceptionOrNull()
                }
            } catch (e: Exception) {
                hasError = true
                lastError = e
            }
        }

        return if (hasError) {
            Result.failure(lastError ?: Exception("Error al exportar registros."))
        } else {
            Result.success(Unit)
        }
    }
}
