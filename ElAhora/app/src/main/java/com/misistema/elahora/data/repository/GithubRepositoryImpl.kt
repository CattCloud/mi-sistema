package com.misistema.elahora.data.repository

import com.misistema.elahora.data.remote.github.GithubApiService
import com.misistema.elahora.data.remote.github.GithubFile
import com.misistema.elahora.data.remote.github.GithubPutRequest
import com.misistema.elahora.domain.repository.GithubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GithubRepositoryImpl(
    private val api: GithubApiService
) : GithubRepository {

    override suspend fun listSistemas(owner: String, repo: String, token: String?): Result<List<GithubFile>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
                val response = api.getSistemas(owner, repo, authHeader)
                Result.success(response.filter { it.name.endsWith(".md") })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun downloadSistema(downloadUrl: String, token: String?): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
                val response = api.downloadRawFile(downloadUrl, authHeader)
                Result.success(response.string())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getFileContent(owner: String, repo: String, path: String, token: String?): Result<GithubFile> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
                val response = api.getFileContent(owner, repo, path, authHeader)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveFile(owner: String, repo: String, path: String, contentBase64: String, commitMessage: String, token: String?): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
                
                // Obtener SHA previo (si el archivo ya existe)
                val sha = try {
                    api.getFileContent(owner, repo, path, authHeader).sha
                } catch (e: Exception) {
                    null // Si no existe (ej. 404), es un archivo nuevo y sha es null
                }

                val request = GithubPutRequest(
                    message = commitMessage,
                    content = contentBase64,
                    sha = sha
                )
                
                api.putFile(owner, repo, path, authHeader, request)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
