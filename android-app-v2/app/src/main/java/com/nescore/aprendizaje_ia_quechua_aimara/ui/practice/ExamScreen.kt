package com.nescore.aprendizaje_ia_quechua_aimara.ui.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.ui.practice.components.ExamProgressHeader
import com.nescore.aprendizaje_ia_quechua_aimara.ui.practice.components.OptionTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    language: String,
    level: String,
    examTitle: String,
    onBack: () -> Unit,
    onFinish: (Int, Int, String, String) -> Unit,
    viewModel: ExamViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadExam(language, level, examTitle)
    }

    LaunchedEffect(uiState.isExamCompleted) {
        if (uiState.isExamCompleted) {
            val exam = uiState.exam
            if (exam != null) {
                onFinish(
                    uiState.score,
                    exam.questions.size,
                    exam.achievement.name,
                    exam.achievement.shareMessage
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.exam?.examTitle ?: "Examen") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val exam = uiState.exam
                val currentQuestion = exam?.questions?.getOrNull(uiState.currentQuestionIndex)

                if (currentQuestion != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        ExamProgressHeader(
                            currentQuestion = uiState.currentQuestionIndex,
                            totalQuestions = exam.questions.size,
                            progress = (uiState.currentQuestionIndex + 1).toFloat() / exam.questions.size
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        currentQuestion.options.forEach { option ->
                            OptionTile(
                                text = option,
                                isSelected = uiState.selectedOption == option,
                                isCorrect = if (uiState.isAnswerChecked) {
                                    if (option == currentQuestion.correctAnswer) true
                                    else if (option == uiState.selectedOption) false
                                    else null
                                } else null,
                                onClick = { viewModel.onOptionSelected(option) },
                                enabled = !uiState.isAnswerChecked
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedVisibility(visible = uiState.isAnswerChecked) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Explicación:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = currentQuestion.explanation)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (uiState.isAnswerChecked) {
                                    viewModel.nextQuestion()
                                } else {
                                    viewModel.checkAnswer()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.selectedOption != null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (uiState.isAnswerChecked) "Siguiente" else "Comprobar")
                        }
                    }
                }
            }
        }
    }
}
