package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.ChatRepository
import javax.inject.Inject

class GetAIResponseUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(prompt: String): Result<String> {
        return repository.getAIResponse(prompt)
    }
}
