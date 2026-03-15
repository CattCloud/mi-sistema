package com.misistema.elahora.domain.model

data class DailyLog(
    val systemId: String,
    val date: String,     // ISO "YYYY-MM-DD"
    val status: LogStatus?, 
    val notes: String?
)

enum class LogStatus {
    DONE, NOT_DONE
}
