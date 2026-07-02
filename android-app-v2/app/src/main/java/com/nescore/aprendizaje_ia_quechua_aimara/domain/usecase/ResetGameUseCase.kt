package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleWord
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleLanguage
import javax.inject.Inject

class ResetGameUseCase @Inject constructor(
    private val getWordUseCase: GetWordUseCase
) {
    suspend operator fun invoke(language: WordleLanguage, category: String): Result<WordleWord> {
        return getWordUseCase(language, category)
    }
}
