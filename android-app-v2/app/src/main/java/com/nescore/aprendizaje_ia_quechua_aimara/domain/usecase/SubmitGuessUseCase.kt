package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.data.datasource.GameLogicDataSource
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.GuessResult
import javax.inject.Inject

class SubmitGuessUseCase @Inject constructor(
    private val gameLogic: GameLogicDataSource
) {
    operator fun invoke(guess: String, target: String): GuessResult {
        return gameLogic.checkGuess(guess, target)
    }
}
