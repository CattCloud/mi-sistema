package com.misistema.elahora.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DailyLogEntity)

    @Query("SELECT * FROM daily_log WHERE systemId = :systemId AND date = :date LIMIT 1")
    suspend fun getByDate(systemId: String, date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_log WHERE systemId = :systemId AND date >= :weekStart ORDER BY date ASC")
    suspend fun getLogsForWeek(systemId: String, weekStart: String): List<DailyLogEntity>
}
