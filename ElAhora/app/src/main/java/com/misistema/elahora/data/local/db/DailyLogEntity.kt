package com.misistema.elahora.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_log")
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val systemId: String,
    val date: String,       // Formato ISO: "2026-03-14"
    val status: String?,    // "DONE" | "NOT_DONE" | null
    val notes: String?
)
