package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleLanguage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetWordUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.ResetGameUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.SubmitGuessUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.ValidateGuessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordleViewModel @Inject constructor(
    private val getWordUseCase: GetWordUseCase,
    private val validateGuessUseCase: ValidateGuessUseCase,
    private val submitGuessUseCase: SubmitGuessUseCase,
    private val resetGameUseCase: ResetGameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordleUiState())
    val uiState: StateFlow<WordleUiState> = _uiState.asStateFlow()

    fun setLanguage(language: WordleLanguage) {
        _uiState.update { it.copy(language = language) }
        startNewGame()
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(category = category) }
        startNewGame()
    }

    fun onLetterInput(letter: String) {
        val targetLength = getTargetWordString().length
        if (_uiState.value.currentGuess.length < targetLength) {
            _uiState.update { it.copy(
                currentGuess = it.currentGuess + letter,
                inputError = null
            ) }
        }
    }

    fun onDeleteLetter() {
        if (_uiState.value.currentGuess.isNotEmpty()) {
            _uiState.update { it.copy(currentGuess = it.currentGuess.dropLast(1)) }
        }
    }

    fun submitGuess() {
        val state = _uiState.value
        val target = getTargetWordString()
        
        val error = validateGuessUseCase(state.currentGuess, target.length)
        if (error != null) {
            _uiState.update { it.copy(inputError = error) }
            return
        }

        val result = submitGuessUseCase(state.currentGuess, target)
        val newGuesses = state.guesses + result
        
        val isWin = result.isCorrect
        val isLost = !isWin && newGuesses.size >= (state.targetWord?.intentos_max ?: 6)

        _uiState.update { it.copy(
            guesses = newGuesses,
            currentGuess = "",
            isWin = isWin,
            isLost = isLost
        ) }
    }

    fun startNewGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, guesses = emptyList(), isWin = false, isLost = false, currentGuess = "") }
            val result = resetGameUseCase(_uiState.value.language, _uiState.value.category)
            
            result.onSuccess { word ->
                _uiState.update { it.copy(targetWord = word, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun getTargetWordString(): String {
        val word = _uiState.value.targetWord ?: return ""
        return when (_uiState.value.language) {
            WordleLanguage.QUECHUA -> word.quechua
            WordleLanguage.AIMARA -> word.aimara
        }.uppercase()
    }
}
