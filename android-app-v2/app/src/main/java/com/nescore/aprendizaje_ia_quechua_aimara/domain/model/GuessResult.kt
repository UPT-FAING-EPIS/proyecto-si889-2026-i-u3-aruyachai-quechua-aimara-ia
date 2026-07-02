package com.nescore.aprendizaje_ia_quechua_aimara.domain.model

import androidx.annotation.Keep

@Keep
data class GuessResult(
    val word: String,
    val statuses: List<LetterStatus>,
    val isCorrect: Boolean
)
