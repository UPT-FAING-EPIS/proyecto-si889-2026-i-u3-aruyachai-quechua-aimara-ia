package com.nescore.aprendizaje_ia_quechua_aimara.data.datasource

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.LetterStatus
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.GuessResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameLogicDataSource @Inject constructor() {

    fun checkGuess(guess: String, target: String): GuessResult {
        val guessUpper = guess.uppercase()
        val targetUpper = target.uppercase()
        val result = MutableList(target.length) { LetterStatus.INCORRECT }
        val targetCharCount = targetUpper.groupingBy { it }.eachCount().toMutableMap()

        // First pass: Correct positions (Green)
        for (i in guessUpper.indices) {
            if (guessUpper[i] == targetUpper[i]) {
                result[i] = LetterStatus.CORRECT
                targetCharCount[guessUpper[i]] = targetCharCount[guessUpper[i]]!! - 1
            }
        }

        // Second pass: Present but wrong position (Yellow)
        for (i in guessUpper.indices) {
            if (result[i] != LetterStatus.CORRECT && targetUpper.contains(guessUpper[i]) && (targetCharCount[guessUpper[i]] ?: 0) > 0) {
                result[i] = LetterStatus.PRESENT
                targetCharCount[guessUpper[i]] = targetCharCount[guessUpper[i]]!! - 1
            }
        }

        return GuessResult(
            word = guessUpper,
            statuses = result,
            isCorrect = guessUpper == targetUpper
        )
    }

    fun validateInput(guess: String): String? {
        val regex = Regex("^[A-ZÑÁÉÍÓÚÜ]+$", RegexOption.IGNORE_CASE)
        if (!regex.matches(guess)) {
            return "Solo se permiten letras."
        }
        if (guess.length < 3 || guess.length > 10) {
            return "La palabra debe tener entre 3 y 10 letras."
        }
        if (guess.uppercase().toSet().size == 1) {
            return "La palabra no puede tener todas las letras iguales."
        }
        return null
    }
}
