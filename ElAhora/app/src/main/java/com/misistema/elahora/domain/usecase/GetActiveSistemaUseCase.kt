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
        val activeId = localRepo.activeSystemId.firstOrNull()
            ?: return Result.failure(Exception("No hay sistema activo configurado"))

        // --- PRIORIDAD 1: Caché local (sistemas descargados previamente de GitHub) ---
        val cachedJson = localRepo.getCachedSystemJson(activeId)
        if (cachedJson != null) {
            try {
                return Result.success(SistemaParser.parseJson(cachedJson))
            } catch (e: Exception) {
                // Si el caché está corrompido, continuar al siguiente nivel
            }
        }

        // --- PRIORIDAD 2: Assets (archivos de fábrica incluidos en el APK) ---
        val assetsJson = localRepo.getLocalSystemJson(activeId)
        if (assetsJson != null) {
            return try {
                Result.success(SistemaParser.parseJson(assetsJson))
            } catch (e: Exception) {
                Result.failure(Exception("Error parseando JSON de assets: ${e.message}"))
            }
        }

        // --- PRIORIDAD 3: GitHub (descarga en tiempo real y guarda en caché) ---
        val token = localRepo.githubToken.firstOrNull()
        val userRepo = localRepo.githubRepo.firstOrNull()
            ?: return Result.failure(Exception("Sin internet y sin datos locales. Configura GitHub o conectate a la red."))

        val parts = userRepo.split("/")
        if (parts.size != 2) return Result.failure(Exception("Repo inválido. Usa formato owner/repo"))

        val listResult = githubRepo.listSistemas(parts[0], parts[1], token)
        if (listResult.isFailure) {
            return Result.failure(Exception("Sistema no encontrado localmente y sin conexión a GitHub."))
        }

        val files = listResult.getOrNull() ?: emptyList()
        val match = files.find { it.name.contains(activeId, ignoreCase = true) }
            ?: return Result.failure(Exception("Sistema '$activeId' no encontrado en GitHub."))

        val downloadResult = githubRepo.downloadSistema(match.downloadUrl, token)
        if (downloadResult.isFailure) {
            return Result.failure(Exception("Error al descargar el sistema desde GitHub."))
        }

        val jsonString = downloadResult.getOrNull() ?: ""

        return try {
            val sistema = SistemaParser.parseJson(jsonString)
            // Guardar en caché para próximas ejecuciones offline
            localRepo.saveCachedSystemJson(activeId, jsonString)
            Result.success(sistema)
        } catch (e: Exception) {
            Result.failure(Exception("Error parseando JSON de GitHub: ${e.message}"))
        }
    }
}
