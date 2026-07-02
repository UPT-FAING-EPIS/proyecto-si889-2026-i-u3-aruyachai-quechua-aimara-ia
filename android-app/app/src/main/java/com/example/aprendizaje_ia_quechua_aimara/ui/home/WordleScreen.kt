package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nescore.aprendizaje_ia_quechua_aimara.data.WordleRepository

/**
 * WordleScreen: Interfaz de usuario para el juego de adivinanza de palabras.
 * Implementa una lógica multilingüe (Quechua/Aimara) y multitemática (Categorías).
 * 
 * Optimizada con scroll vertical y teclado adaptativo para diferentes tamaños de pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordleScreen(
    onBack: () -> Unit = {}
) {
    val viewModel: WordleViewModel = viewModel(
        factory = WordleViewModelFactory(WordleRepository())
    )
    
    val gameState by viewModel.gameState.collectAsState()
    val guesses by viewModel.guesses.collectAsState()
    val results by viewModel.results.collectAsState()
    val languageMode by viewModel.languageMode.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()
    
    var currentGuess by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (viewModel.gameState.value is WordleGameState.Idle) {
            viewModel.startNewGame(currentCategory)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Permite deslizar de arriba hacia abajo
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera interna para Wordle
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Wordle",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Row {
                FilterChip(
                    selected = languageMode == WordleLanguage.QUECHUA,
                    onClick = { 
                        viewModel.setLanguage(WordleLanguage.QUECHUA)
                        currentGuess = "" 
                    },
                    label = { Text("Quechua", fontSize = 10.sp) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                FilterChip(
                    selected = languageMode == WordleLanguage.AIMARA,
                    onClick = { 
                        viewModel.setLanguage(WordleLanguage.AIMARA)
                        currentGuess = ""
                    },
                    label = { Text("Aimara", fontSize = 10.sp) }
                )
            }
        }

        // Selección de categoría
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val categorias = listOf("animales", "colores", "cuerpohumano")
            categorias.forEach { cat ->
                FilterChip(
                    selected = currentCategory == cat,
                    onClick = { 
                        viewModel.setCategory(cat)
                        currentGuess = ""
                    },
                    label = { 
                        Text(
                            text = when(cat) {
                                "cuerpohumano" -> "Cuerpo"
                                else -> cat.replaceFirstChar { it.uppercase() }
                            },
                            fontSize = 10.sp
                        ) 
                    },
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }

        when (val state = gameState) {
            is WordleGameState.Loading -> {
                Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WordleGameState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.startNewGame(currentCategory) }) {
                        Text("Reintentar carga")
                    }
                }
            }
            is WordleGameState.Playing, is WordleGameState.Won, is WordleGameState.Lost -> {
                val wordleWord = when (state) {
                    is WordleGameState.Playing -> state.word
                    is WordleGameState.Won -> state.word
                    is WordleGameState.Lost -> state.word
                    else -> null
                }

                if (wordleWord != null) {
                    val targetLen = if (languageMode == WordleLanguage.QUECHUA) 
                        wordleWord.quechua.length else wordleWord.aimara.length

                    WordleGrid(
                        wordLength = targetLen,
                        maxGuesses = wordleWord.intentos_max,
                        guesses = guesses,
                        results = results,
                        currentGuess = currentGuess
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state is WordleGameState.Won) {
                        Text("¡Felicidades! Ganaste.", 
                            color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                        Button(onClick = { 
                            viewModel.startNewGame(currentCategory)
                            currentGuess = ""
                        }) {
                            Text("Jugar de nuevo")
                        }
                    } else if (state is WordleGameState.Lost) {
                        val correctWord = if (languageMode == WordleLanguage.QUECHUA) 
                            wordleWord.quechua else wordleWord.aimara
                        
                        Text("Perdiste. Era: $correctWord", 
                            color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                        Button(onClick = { 
                            viewModel.startNewGame(currentCategory)
                            currentGuess = ""
                        }) {
                            Text("Reintentar")
                        }
                    } else {
                        WordleKeyboard(
                            onKeyClick = { char ->
                                if (currentGuess.length < targetLen) {
                                    currentGuess += char
                                }
                            },
                            onDeleteClick = {
                                if (currentGuess.isNotEmpty()) {
                                    currentGuess = currentGuess.dropLast(1)
                                }
                            },
                            onEnterClick = {
                                if (currentGuess.length == targetLen) {
                                    viewModel.submitGuess(currentGuess)
                                    currentGuess = ""
                                }
                            }
                        )
                    }
                }
            }
            else -> {}
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * WordleGrid: Organiza las filas de intentos y la fila activa de escritura.
 * Usa un tamaño fijo para los cuadros para que no varíen según el largo de la palabra.
 */
@Composable
fun WordleGrid(
    wordLength: Int,
    maxGuesses: Int,
    guesses: List<String>,
    results: List<List<LetterStatus>>,
    currentGuess: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(maxGuesses) { rowIndex ->
            val rowGuess = guesses.getOrNull(rowIndex)
            val rowResult = results.getOrNull(rowIndex)
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                repeat(wordLength) { colIndex ->
                    val char = when {
                        rowGuess != null -> rowGuess.getOrNull(colIndex)?.toString() ?: ""
                        rowIndex == guesses.size -> currentGuess.getOrNull(colIndex)?.toString() ?: ""
                        else -> ""
                    }
                    
                    val status = rowResult?.getOrNull(colIndex)
                    // Tamaño fijo de 42dp para consistencia
                    Box(modifier = Modifier.size(42.dp)) {
                        WordleLetterBox(char, status)
                    }
                }
            }
        }
    }
}

/**
 * WordleLetterBox: Representa una celda individual de la cuadrícula.
 */
@Composable
fun WordleLetterBox(char: String, status: LetterStatus?) {
    val backgroundColor = when (status) {
        LetterStatus.Correct -> Color(0xFF6AAA64) 
        LetterStatus.Present -> Color(0xFFC9B458) 
        LetterStatus.Incorrect -> Color(0xFF787C7E) 
        null -> Color.Transparent
    }
    
    val borderColor = if (status == null && char.isNotEmpty()) {
        MaterialTheme.colorScheme.outline
    } else if (status == null) {
        MaterialTheme.colorScheme.outlineVariant
    } else {
        backgroundColor
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.uppercase(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (status != null) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * WordleKeyboard: Teclado virtual adaptativo.
 */
@Composable
fun WordleKeyboard(
    onKeyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onEnterClick: () -> Unit
) {
    val rows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L", "Ñ"),
        listOf("ENTER", "Z", "X", "C", "V", "B", "N", "M", "DEL")
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { key ->
                    val weight = when (key) {
                        "ENTER", "DEL" -> 1.5f
                        else -> 1f
                    }
                    KeyboardKey(
                        key = key,
                        onClick = {
                            when (key) {
                                "ENTER" -> onEnterClick()
                                "DEL" -> onDeleteClick()
                                else -> onKeyClick(key)
                            }
                        },
                        modifier = Modifier.weight(weight)
                    )
                }
            }
        }
    }
}

/**
 * KeyboardKey: Componente base para cada tecla del teclado virtual.
 */
@Composable
fun KeyboardKey(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (key == "DEL") {
                Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete", modifier = Modifier.size(18.dp))
            } else {
                Text(
                    text = key,
                    fontSize = if (key == "ENTER") 10.sp else 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

class WordleViewModelFactory(private val repository: WordleRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return WordleViewModel(repository) as T
    }
}
