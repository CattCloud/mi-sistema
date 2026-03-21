package com.misistema.elahora.di

import android.content.Context
import androidx.room.Room
import com.misistema.elahora.data.local.datastore.SistemaPreferences
import com.misistema.elahora.data.local.db.AppDatabase
import com.misistema.elahora.data.local.db.DailyLogDao
import com.misistema.elahora.data.remote.github.GithubApiService
import com.misistema.elahora.data.repository.GithubRepositoryImpl
import com.misistema.elahora.data.repository.LocalRepositoryImpl
import com.misistema.elahora.domain.repository.GithubRepository
import com.misistema.elahora.domain.repository.LocalRepository
import com.misistema.elahora.domain.usecase.GetActiveSistemaUseCase
import com.misistema.elahora.domain.usecase.GetWeekLogsUseCase
import com.misistema.elahora.domain.usecase.ListSistemasUseCase
import com.misistema.elahora.domain.usecase.SaveDailyLogUseCase
import com.misistema.elahora.domain.usecase.ExportLogsToGithubUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- LOCAL DATA ---
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "elahora_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDailyLogDao(db: AppDatabase): DailyLogDao {
        return db.dailyLogDao()
    }

    @Provides
    @Singleton
    fun provideSistemaPreferences(@ApplicationContext context: Context): SistemaPreferences {
        return SistemaPreferences(context)
    }

    @Provides
    @Singleton
    fun provideLocalRepository(
        @ApplicationContext context: Context,
        dao: DailyLogDao,
        prefs: SistemaPreferences
    ): LocalRepository {
        return LocalRepositoryImpl(context, dao, prefs)
    }

    // --- REMOTE DATA (GITHUB API) ---
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideGithubApiService(client: OkHttpClient): GithubApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGithubRepository(api: GithubApiService): GithubRepository {
        return GithubRepositoryImpl(api)
    }

    // --- USE CASES ---
    @Provides
    @Singleton
    fun provideListSistemasUseCase(
        githubRepo: GithubRepository,
        localRepo: LocalRepository
    ): ListSistemasUseCase {
        return ListSistemasUseCase(githubRepo, localRepo)
    }

    @Provides
    @Singleton
    fun provideGetActiveSistemaUseCase(
        githubRepo: GithubRepository,
        localRepo: LocalRepository
    ): GetActiveSistemaUseCase {
        return GetActiveSistemaUseCase(githubRepo, localRepo)
    }

    @Provides
    @Singleton
    fun provideSaveDailyLogUseCase(localRepo: LocalRepository): SaveDailyLogUseCase {
        return SaveDailyLogUseCase(localRepo)
    }

    @Provides
    @Singleton
    fun provideGetWeekLogsUseCase(localRepo: LocalRepository): GetWeekLogsUseCase {
        return GetWeekLogsUseCase(localRepo)
    }

    @Provides
    @Singleton
    fun provideExportLogsToGithubUseCase(
        githubRepo: GithubRepository,
        localRepo: LocalRepository
    ): ExportLogsToGithubUseCase {
        return ExportLogsToGithubUseCase(githubRepo, localRepo)
    }
}
