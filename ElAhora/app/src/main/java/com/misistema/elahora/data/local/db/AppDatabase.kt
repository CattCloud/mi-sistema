package com.misistema.elahora.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao
}
