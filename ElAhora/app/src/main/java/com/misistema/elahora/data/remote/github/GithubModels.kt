package com.misistema.elahora.data.remote.github

import com.google.gson.annotations.SerializedName

data class GithubFile(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("git_url") val gitUrl: String,
    @SerializedName("download_url") val downloadUrl: String,
    val type: String
)
