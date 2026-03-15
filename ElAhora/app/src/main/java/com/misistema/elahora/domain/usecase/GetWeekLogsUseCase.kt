package com.misistema.elahora.domain.usecase

import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.repository.LocalRepository

class GetWeekLogsUseCase(
    private val localRepo: LocalRepository
) {
    suspend operator fun invoke(systemId: String, weekStart: String): List<DailyLog> {
        return localRepo.getWeekLogs(systemId, weekStart)
    }
}
