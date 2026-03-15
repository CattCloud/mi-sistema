package com.misistema.elahora.domain.usecase

import com.misistema.elahora.data.remote.github.GithubFile
import com.misistema.elahora.domain.repository.GithubRepository
import com.misistema.elahora.domain.repository.LocalRepository
import kotlinx.coroutines.flow.firstOrNull

class ListSistemasUseCase(
    private val githubRepo: GithubRepository,
    private val localRepo: LocalRepository
) {
    suspend operator fun invoke(): Result<List<GithubFile>> {
        val userRepo = localRepo.githubRepo.firstOrNull() ?: return Result.failure(Exception("Repositorio no configurado en Settings"))
        val token = localRepo.githubToken.firstOrNull()
        
        // El userRepo suele venir en formato "owner/repo"
        val parts = userRepo.split("/")
        if (parts.size != 2) return Result.failure(Exception("Formato de repositorio inválido (esperado: owner/repo)"))
        
        return githubRepo.listSistemas(parts[0], parts[1], token)
    }
}
