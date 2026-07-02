package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.data.datasource.GameLogicDataSource
import javax.inject.Inject

class ValidateGuessUseCase @Inject constructor(
    private val gameLogic: GameLogicDataSource
) {
    operator fun invoke(guess: String, targetLength: Int): String? {
        val basicValidationError = gameLogic.validateInput(guess)
        if (basicValidationError != null) return basicValidationError
        
        if (guess.length != targetLength) {
            return "La palabra debe tener $targetLength letras."
        }
        
        return null
    }
}
