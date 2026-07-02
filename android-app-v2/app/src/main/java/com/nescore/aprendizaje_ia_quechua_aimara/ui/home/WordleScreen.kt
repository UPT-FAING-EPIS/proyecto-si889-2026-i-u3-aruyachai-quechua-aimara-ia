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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleLanguage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.LetterStatus
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.GuessResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordleScreen(
    viewModel: WordleViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.targetWord == null) {
            viewModel.startNewGame()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                WordleLanguage.values().forEach { lang ->
                    FilterChip(
                        selected = uiState.language == lang,
                        onClick = { viewModel.setLanguage(lang) },
                        label = { Text(lang.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp) },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val categorias = listOf("animales", "colores", "cuerpohumano")
            categorias.forEach { cat ->
                FilterChip(
                    selected = uiState.category == cat,
                    onClick = { viewModel.setCategory(cat) },
                    label = { 
                        Text(
                            text = if(cat == "cuerpohumano") "Cuerpo" else cat.replaceFirstChar { it.uppercase() },
                            fontSize = 10.sp
                        ) 
                    },
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.startNewGame() }) {
                    Text("Reintentar")
                }
            }
        } else {
            uiState.targetWord?.let { wordleWord ->
                val targetStr = when (uiState.language) {
                    WordleLanguage.QUECHUA -> wordleWord.quechua
                    WordleLanguage.AIMARA -> wordleWord.aimara
                }

                WordleGrid(
                    wordLength = targetStr.length,
                    maxGuesses = wordleWord.intentos_max,
                    guesses = uiState.guesses,
                    currentGuess = uiState.currentGuess
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.isWin) {
                    Text("¡Felicidades! Ganaste.", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    Button(onClick = { viewModel.startNewGame() }) { Text("Jugar de nuevo") }
                } else if (uiState.isLost) {
                    Text("Perdiste. Era: ${targetStr.uppercase()}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Button(onClick = { viewModel.startNewGame() }) { Text("Reintentar") }
                } else {
                    WordleKeyboard(
                        onKeyClick = { viewModel.onLetterInput(it) },
                        onDeleteClick = { viewModel.onDeleteLetter() },
                        onEnterClick = { viewModel.submitGuess() }
                    )
                }
            }
        }
        
        uiState.inputError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun WordleGrid(
    wordLength: Int,
    maxGuesses: Int,
    guesses: List<GuessResult>,
    currentGuess: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(maxGuesses) { rowIndex ->
            val guessResult = guesses.getOrNull(rowIndex)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(wordLength) { colIndex ->
                    val char = when {
                        guessResult != null -> guessResult.word.getOrNull(colIndex)?.toString() ?: ""
                        rowIndex == guesses.size -> currentGuess.getOrNull(colIndex)?.toString() ?: ""
                        else -> ""
                    }
                    val status = guessResult?.statuses?.getOrNull(colIndex) ?: LetterStatus.EMPTY
                    WordleLetterBox(char, status)
                }
            }
        }
    }
}

@Composable
fun WordleLetterBox(char: String, status: LetterStatus) {
    val backgroundColor = when (status) {
        LetterStatus.CORRECT -> Color(0xFF6AAA64)
        LetterStatus.PRESENT -> Color(0xFFC9B458)
        LetterStatus.INCORRECT -> Color(0xFF787C7E)
        LetterStatus.EMPTY -> Color.Transparent
    }
    
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(1.5.dp, if (status == LetterStatus.EMPTY) MaterialTheme.colorScheme.outline else backgroundColor, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(char.uppercase(), fontWeight = FontWeight.Bold, color = if (status != LetterStatus.EMPTY) Color.White else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun WordleKeyboard(onKeyClick: (String) -> Unit, onDeleteClick: () -> Unit, onEnterClick: () -> Unit) {
    val rows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L", "Ñ"),
        listOf("ENTER", "Z", "X", "C", "V", "B", "N", "M", "DEL")
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { key ->
                    Button(
                        onClick = { when(key) { "ENTER" -> onEnterClick(); "DEL" -> onDeleteClick(); else -> onKeyClick(key) } },
                        modifier = Modifier.height(50.dp).weight(if (key == "ENTER" || key == "DEL") 1.5f else 1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        if (key == "DEL") Icon(Icons.AutoMirrored.Filled.Backspace, null, modifier = Modifier.size(18.dp))
                        else Text(key, fontSize = if (key == "ENTER") 10.sp else 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
