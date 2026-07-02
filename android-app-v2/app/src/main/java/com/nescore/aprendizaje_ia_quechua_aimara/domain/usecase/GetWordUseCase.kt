package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleWord
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleLanguage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.WordleRepository
import javax.inject.Inject

class GetWordUseCase @Inject constructor(
    private val repository: WordleRepository
) {
    suspend operator fun invoke(language: WordleLanguage, category: String): Result<WordleWord> {
        return repository.getRandomWord(language, category)
    }
}
