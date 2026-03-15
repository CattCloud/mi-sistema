package com.misistema.elahora.domain.repository

import com.misistema.elahora.data.remote.github.GithubFile

interface GithubRepository {
    suspend fun listSistemas(owner: String, repo: String, token: String?): Result<List<GithubFile>>
    suspend fun downloadSistema(downloadUrl: String, token: String?): Result<String>
}
