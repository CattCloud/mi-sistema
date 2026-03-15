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
        val token = localRepo.githubToken.firstOrNull()
        val userRepo = localRepo.githubRepo.firstOrNull() ?: return Result.failure(Exception("Repositorio Github no configurado"))

        val parts = userRepo.split("/")
        if (parts.size != 2) return Result.failure(Exception("Repo inválido"))

        // Se requiere hacer fetch de la lista primero para buscar el downloadUrl, dado que la API raw pide el url específico o requiere un path 
        val listResult = githubRepo.listSistemas(parts[0], parts[1], token)
        if (listResult.isFailure) return Result.failure(listResult.exceptionOrNull()!!)

        val files = listResult.getOrNull() ?: emptyList()
        val match = files.find { it.name.contains(activeId, ignoreCase = true) } 
            ?: return Result.failure(Exception("Archivo del sistema no encontrado en el repositorio remoto"))

        val downloadResult = githubRepo.downloadSistema(match.downloadUrl, token)
        if (downloadResult.isFailure) return Result.failure(downloadResult.exceptionOrNull()!!)

        val markdown = downloadResult.getOrNull() ?: ""
        val sistema = SistemaParser.parse(activeId, markdown)
        
        return Result.success(sistema)
    }
}
