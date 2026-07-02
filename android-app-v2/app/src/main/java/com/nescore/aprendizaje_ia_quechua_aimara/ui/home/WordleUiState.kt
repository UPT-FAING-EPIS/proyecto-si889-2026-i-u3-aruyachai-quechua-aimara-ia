package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleWord
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleLanguage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.GuessResult

data class WordleUiState(
    val language: WordleLanguage = WordleLanguage.QUECHUA,
    val category: String = "animales",
    val targetWord: WordleWord? = null,
    val guesses: List<GuessResult> = emptyList(),
    val currentGuess: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isWin: Boolean = false,
    val isLost: Boolean = false,
    val inputError: String? = null
) {
    val isGameOver: Boolean get() = isWin || isLost
    val attemptsLeft: Int get() = (targetWord?.intentos_max ?: 6) - guesses.size
}
