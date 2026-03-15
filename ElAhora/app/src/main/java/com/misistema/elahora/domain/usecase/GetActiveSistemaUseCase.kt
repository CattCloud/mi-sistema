package com.misistema.elahora.domain.usecase

import com.misistema.elahora.domain.model.Sistema
import com.misistema.elahora.domain.model.SistemaParser
import com.misistema.elahora.domain.repository.GithubRepository
import com.misistema.elahora.domain.repository.LocalRepository
import kotlinx.coroutines.flow.firstOrNull

class GetActiveSistemaUseCase(
    private val githubRepo: GithubRepository,
    private val localRepo: LocalRepository
) {
    suspend operator fun invoke(): Result<Sistema> {
        val activeId = localRepo.activeSystemId.firstOrNull() ?: return Result.failure(Exception("No hay sistema activo configurado"))
        
        // 1. Intentar cargar desde Local Assets primero (como prioridad para este dev loop local)
        val localJson = localRepo.getLocalSystemJson(activeId)
        if (localJson != null) {
            return try {
                Result.success(SistemaParser.parseJson(localJson))
            } catch (e: Exception) {
                Result.failure(Exception("Error parseando JSON local de activos: ${e.message}"))
            }
        }
        
        // 2. Si no existe en local, intentar desde Github 
        val token = localRepo.githubToken.firstOrNull()
        val userRepo = localRepo.githubRepo.firstOrNull() ?: return Result.failure(Exception("Repositorio Github no configurado"))

        val parts = userRepo.split("/")
        if (parts.size != 2) return Result.failure(Exception("Repo inválido"))

        val listResult = githubRepo.listSistemas(parts[0], parts[1], token)
        if (listResult.isFailure) return Result.failure(listResult.exceptionOrNull()!!)

        val files = listResult.getOrNull() ?: emptyList()
        // Buscar el archivo con ese ID (probablemente termine en .json ahora)
        val match = files.find { it.name.contains(activeId, ignoreCase = true) } 
            ?: return Result.failure(Exception("Archivo del sistema no encontrado (ni en local ni en remoto)"))

        val downloadResult = githubRepo.downloadSistema(match.downloadUrl, token)
        if (downloadResult.isFailure) return Result.failure(downloadResult.exceptionOrNull()!!)

        val jsonString = downloadResult.getOrNull() ?: ""
        return try {
            val sistema = SistemaParser.parseJson(jsonString)
            Result.success(sistema)
        } catch (e: Exception) {
            Result.failure(Exception("Error parseando JSON remoto: ${e.message}"))
        }
    }
}
