package com.misistema.elahora.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SistemaPreferences(private val context: Context) {

    companion object {
        val ACTIVE_SYSTEM_ID = stringPreferencesKey("active_system_id")
        val GITHUB_TOKEN = stringPreferencesKey("github_token")
        val GITHUB_REPO = stringPreferencesKey("github_repo")
        val PHRASES_JSON = stringPreferencesKey("phrases_json")
    }

    val activeSystemId: Flow<String?> = context.dataStore.data.map { it[ACTIVE_SYSTEM_ID] }
    suspend fun saveActiveSystemId(id: String) {
        context.dataStore.edit { it[ACTIVE_SYSTEM_ID] = id }
    }

    val githubToken: Flow<String?> = context.dataStore.data.map { it[GITHUB_TOKEN] }
    suspend fun saveGithubToken(token: String) {
        context.dataStore.edit { it[GITHUB_TOKEN] = token }
    }

    val githubRepo: Flow<String?> = context.dataStore.data.map { it[GITHUB_REPO] }
    suspend fun saveGithubRepo(repo: String) {
        context.dataStore.edit { it[GITHUB_REPO] = repo }
    }

    val phrasesJson: Flow<String?> = context.dataStore.data.map { it[PHRASES_JSON] }
    suspend fun savePhrasesJson(json: String) {
        context.dataStore.edit { it[PHRASES_JSON] = json }
    }
}
