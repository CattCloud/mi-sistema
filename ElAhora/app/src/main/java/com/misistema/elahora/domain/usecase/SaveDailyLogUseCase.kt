package com.misistema.elahora.domain.usecase

import com.misistema.elahora.domain.model.DailyLog
import com.misistema.elahora.domain.repository.LocalRepository

class SaveDailyLogUseCase(
    private val localRepo: LocalRepository
) {
    suspend operator fun invoke(log: DailyLog) {
        localRepo.saveDailyLog(log)
    }
}
