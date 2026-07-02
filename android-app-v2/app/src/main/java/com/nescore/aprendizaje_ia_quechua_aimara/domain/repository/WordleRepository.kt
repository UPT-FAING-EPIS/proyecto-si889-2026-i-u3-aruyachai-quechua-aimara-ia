package com.nescore.aprendizaje_ia_quechua_aimara.domain.repository

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleWord
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleLanguage
import kotlinx.coroutines.flow.Flow

interface WordleRepository {
    suspend fun getRandomWord(language: WordleLanguage, category: String): Result<WordleWord>
    fun getAllWords(): Flow<List<WordleWord>>
    suspend fun saveWords(words: List<WordleWord>)
    suspend fun clearWords()
}
