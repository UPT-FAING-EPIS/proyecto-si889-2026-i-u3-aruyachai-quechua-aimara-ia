package com.nescore.aprendizaje_ia_quechua_aimara.ui.practice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.ui.practice.components.PracticeCard

@Composable
fun PracticeCategoryScreen(
    onCategorySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Elige un idioma para practicar",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, top = 16.dp),
            textAlign = TextAlign.Center
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                PracticeCard(
                    title = "Quechua",
                    subtitle = "Practica el idioma de los Incas",
                    icon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.fillMaxSize()) },
                    onClick = { onCategorySelected("Quechua") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            item {
                PracticeCard(
                    title = "Aimara",
                    subtitle = "Practica la lengua del Altiplano",
                    icon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.fillMaxSize()) },
                    onClick = { onCategorySelected("Aimara") },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeListScreen(
    language: String,
    viewModel: PracticeViewModel = hiltViewModel(),
    onPracticeSelected: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val levels = listOf("Fácil", "Normal", "Difícil")

    LaunchedEffect(language) {
        viewModel.init(language)
    }

    LaunchedEffect(Unit) {
        viewModel.loadCompletedPractices()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prácticas de $language") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Selector de Dificultad
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                levels.forEachIndexed { index, level ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = levels.size),
                        onClick = { viewModel.setLevel(level) },
                        selected = uiState.selectedLevel == level
                    ) {
                        Text(level)
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.practices) { practice ->
                        val safeTitle = practice.examTitle
                            .replace(".", "_")
                            .replace("#", "_")
                            .replace("$", "_")
                            .replace("[", "_")
                            .replace("]", "_")
                            .replace("/", "_")
                        val isCompleted = uiState.completedPractices.contains(safeTitle)

                        Card(
                            onClick = { onPracticeSelected(language, uiState.selectedLevel, practice.examTitle) },
                            modifier = Modifier.fillMaxWidth(),
                            border = if (isCompleted) BorderStroke(1.5.dp, Color(0xFF4CAF50)) else null,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCompleted) {
                                    Color(0xFF4CAF50).copy(alpha = 0.12f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = practice.examTitle,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Completado",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = practice.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isCompleted) {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SuggestionChip(
                                        onClick = { },
                                        label = { 
                                            Text("${practice.questions.size} preguntas") 
                                        },
                                        enabled = false
                                    )
                                    if (isCompleted) {
                                        SuggestionChip(
                                            onClick = { },
                                            label = { 
                                                Text("Completado", color = Color(0xFF2E7D32)) 
                                            },
                                            enabled = false,
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                disabledLabelColor = Color(0xFF2E7D32)
                                            ),
                                            border = SuggestionChipDefaults.suggestionChipBorder(
                                                enabled = false,
                                                disabledBorderColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.practices.isEmpty()) {
                        item {
                            Text(
                                text = "No hay prácticas disponibles para este nivel.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
