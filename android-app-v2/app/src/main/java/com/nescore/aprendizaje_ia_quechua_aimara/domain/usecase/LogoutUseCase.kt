package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
