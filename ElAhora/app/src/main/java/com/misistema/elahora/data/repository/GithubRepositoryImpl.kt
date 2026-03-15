package com.misistema.elahora.data.repository

import com.misistema.elahora.data.remote.github.GithubApiService
import com.misistema.elahora.data.remote.github.GithubFile
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
}
