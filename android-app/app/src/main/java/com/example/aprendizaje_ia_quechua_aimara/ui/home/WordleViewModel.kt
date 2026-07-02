package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nescore.aprendizaje_ia_quechua_aimara.data.WordleRepository
import com.nescore.aprendizaje_ia_quechua_aimara.data.model.WordleWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * WordleViewModel: Gestiona la lógica del juego Wordle con soporte para múltiples idiomas y categorías.
 */
class WordleViewModel(private val repository: WordleRepository) : ViewModel() {

    // Modo de idioma actual (Quechua o Aimara)
    private val _languageMode = MutableStateFlow(WordleLanguage.QUECHUA)
    val languageMode: StateFlow<WordleLanguage> = _languageMode.asStateFlow()

    // Categoría actual del juego
    private val _currentCategory = MutableStateFlow("animales")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    // Estado actual del juego
    private val _gameState = MutableStateFlow<WordleGameState>(WordleGameState.Idle)
    val gameState: StateFlow<WordleGameState> = _gameState.asStateFlow()

    // Nuevo estado para manejar errores de validación de entrada
    private val _inputError = MutableStateFlow<String?>(null)
    val inputError: StateFlow<String?> = _inputError.asStateFlow()

    // Intentos del usuario
    private val _guesses = MutableStateFlow<List<String>>(emptyList())
    val guesses: StateFlow<List<String>> = _guesses.asStateFlow()

    // Resultados de colores por cada intento
    private val _results = MutableStateFlow<List<List<LetterStatus>>>(emptyList())
    val results: StateFlow<List<List<LetterStatus>>> = _results.asStateFlow()

    private var targetWord: WordleWord? = null
    private var currentTargetString: String = ""

    /**
     * Cambia el idioma y reinicia el juego.
     */
    fun setLanguage(language: WordleLanguage) {
        _languageMode.value = language
        startNewGame(_currentCategory.value)
    }

    /**
     * Cambia la categoría y reinicia el juego.
     */
    fun setCategory(categoria: String) {
        _currentCategory.value = categoria
        startNewGame(categoria)
    }

    /**
     * Inicia una nueva partida obteniendo una palabra aleatoria según el modo y categoría.
     */
    fun startNewGame(categoria: String) {
        viewModelScope.launch {
            _gameState.value = WordleGameState.Loading
            _inputError.value = null // Limpiar errores al iniciar
            val word = repository.getRandomWord(_languageMode.value, categoria)
            
            if (word != null) {
                targetWord = word
                currentTargetString = if (_languageMode.value == WordleLanguage.QUECHUA) {
                    word.quechua.uppercase()
                } else {
                    word.aimara.uppercase()
                }
                
                _guesses.value = emptyList()
                _results.value = emptyList()
                _gameState.value = WordleGameState.Playing(word)
            } else {
                _gameState.value = WordleGameState.Error("No se encontraron palabras para: $categoria")
            }
        }
    }

    /**
     * Valida la palabra ingresada por el usuario antes de procesarla.
     * @param guess Palabra a validar.
     * @return true si es válida, false de lo contrario.
     */
    private fun validateInput(guess: String): Boolean {
        // Regla 1: Solo permitir letras (A-Z) y caracteres especiales como Ñ o tildes
        val regex = Regex("^[A-ZÑÁÉÍÓÚÜ]+$", RegexOption.IGNORE_CASE)
        if (!regex.matches(guess)) {
            _inputError.value = "Solo se permiten letras (sin números ni símbolos)."
            return false
        }

        // Regla 2: Longitud mínima 3 y máxima 10
        if (guess.length < 3 || guess.length > 10) {
            _inputError.value = "La palabra debe tener entre 3 y 10 letras."
            return false
        }

        // Regla 3: No permitir todas las letras iguales (ej: "ssssss")
        if (guess.uppercase().toSet().size == 1) {
            _inputError.value = "La palabra no puede tener todas las letras iguales."
            return false
        }

        _inputError.value = null // Palabra válida
        return true
    }

    /**
     * Procesa el intento del usuario tras realizar la validación.
     */
    fun submitGuess(guess: String) {
        // Realizar validación técnica
        if (!validateInput(guess)) return

        val target = currentTargetString
        // Validación de longitud contra la palabra objetivo actual
        if (guess.length != target.length) {
            _inputError.value = "La palabra debe tener ${target.length} letras."
            return
        }

        val currentGuesses = _guesses.value.toMutableList()
        currentGuesses.add(guess.uppercase())
        _guesses.value = currentGuesses

        // Validación de letras (Lógica Wordle)
        val result = checkGuess(guess.uppercase(), target)
        val currentResults = _results.value.toMutableList()
        currentResults.add(result)
        _results.value = currentResults

        // Verificar resultado final
        if (guess.uppercase() == target) {
            _gameState.value = WordleGameState.Won(targetWord!!)
        } else if (currentGuesses.size >= (targetWord?.intentos_max ?: 6)) {
            _gameState.value = WordleGameState.Lost(targetWord!!)
        }
    }

    private fun checkGuess(guess: String, target: String): List<LetterStatus> {
        val result = MutableList(target.length) { LetterStatus.Incorrect }
        val targetCharCount = target.groupingBy { it }.eachCount().toMutableMap()

        // Primera pasada: Verdes
        for (i in guess.indices) {
            if (guess[i] == target[i]) {
                result[i] = LetterStatus.Correct
                targetCharCount[guess[i]] = targetCharCount[guess[i]]!! - 1
            }
        }

        // Segunda pasada: Amarillos
        for (i in guess.indices) {
            if (result[i] != LetterStatus.Correct && target.contains(guess[i]) && (targetCharCount[guess[i]] ?: 0) > 0) {
                result[i] = LetterStatus.Present
                targetCharCount[guess[i]] = targetCharCount[guess[i]]!! - 1
            }
        }

        return result
    }

    /**
     * Limpia el estado de error de entrada.
     */
    fun clearInputError() {
        _inputError.value = null
    }
}

sealed class WordleGameState {
    object Idle : WordleGameState()
    object Loading : WordleGameState()
    data class Playing(val word: WordleWord) : WordleGameState()
    data class Won(val word: WordleWord) : WordleGameState()
    data class Lost(val word: WordleWord) : WordleGameState()
    data class Error(val message: String) : WordleGameState()
}

enum class LetterStatus {
    Correct, Present, Incorrect
}
