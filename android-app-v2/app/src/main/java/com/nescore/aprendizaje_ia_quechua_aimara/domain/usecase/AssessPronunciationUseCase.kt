package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.ChatRepository
import javax.inject.Inject

class AssessPronunciationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        audioPath: String,
        targetWord: String,
        language: String,
        translation: String
    ): Result<Map<String, Any>> {
        return chatRepository.assessPronunciation(audioPath, targetWord, language, translation)
    }
}
