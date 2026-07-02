package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.TemasRepository
import javax.inject.Inject

class GetTemasUseCase @Inject constructor(
    private val repository: TemasRepository
) {
    suspend operator fun invoke(): Result<List<Tema>> = repository.getTemas()
}
