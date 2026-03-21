package com.misistema.elahora.data.remote.github

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url

interface GithubApiService {
    
    // Lista los archivos de la subcarpeta "sistemas"
    @GET("repos/{owner}/{repo}/contents/sistemas")
    suspend fun getSistemas(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authHeader: String?
    ): List<GithubFile>

    // Descarga el contenido crudo (raw) de un archivo .md usando su downloadUrl
    @GET
    suspend fun downloadRawFile(
        @Url url: String,
        @Header("Authorization") authHeader: String?
    ): ResponseBody

    // Obtener info de un archivo específico (para sacar el SHA y poder sobreescribir)
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path", encoded = true) path: String,
        @Header("Authorization") authHeader: String?
    ): GithubFile

    // Crear o sobreescribir un archivo
    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun putFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path", encoded = true) path: String,
        @Header("Authorization") authHeader: String?,
        @Body body: GithubPutRequest
    ): ResponseBody
}
