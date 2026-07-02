package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyGuideScreen(
    selectedLanguage: String,
    viewModel: StudyGuideViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Synchronize language state
    LaunchedEffect(selectedLanguage) {
        viewModel.setLanguage(selectedLanguage)
    }

    // Permission launcher for microphone
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    if (uiState.activeTema == null) {
        // Selection State: List of lessons/topics
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Language Selection Tabs (Quechua vs. Aimara)
                TabRow(
                    selectedTabIndex = if (uiState.language.lowercase() == "quechua") 0 else 1,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = uiState.language.lowercase() == "quechua",
                        onClick = { viewModel.setLanguage("quechua") },
                        text = { Text("Quechua", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = uiState.language.lowercase() == "aimara",
                        onClick = { viewModel.setLanguage("aimara") },
                        text = { Text("Aimara", fontWeight = FontWeight.Bold) }
                    )
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.error != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadTemas() }) {
                            Text("Reintentar")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.temas) { tema ->
                            val completed = uiState.progress[tema.id] ?: 0
                            val totalWords = 20
                            val fraction = (completed.toFloat() / totalWords).coerceIn(0f, 1f)
                            // Visual cards representing lessons
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectTema(tema) },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = tema.nombre,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(
                                            onClick = { viewModel.selectTema(tema) },
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar")
                                        }
                                    }
                                    Text(
                                        text = tema.descripcion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Progress Bar instead of plain text
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { fraction },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "$completed/$totalWords",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Lesson State: Step-by-step interactive card study
        val activeTema = uiState.activeTema!!
        val words = uiState.palabras
        val currentWordIndex = uiState.currentWordIndex
        val word = words.getOrNull(currentWordIndex)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(activeTema.nombre, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.deselectTema() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (uiState.isLoading || word == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val progressFraction = (currentWordIndex.toFloat() + 1) / words.size

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Palabra ${currentWordIndex + 1} de ${words.size}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(progressFraction * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Elegant interactive vocabulary card (scroll-safe wrapped size)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Badge indicating if completed or pending
                            val currentWordEspanol = word.espanol
                            val safeKey = currentWordEspanol
                                .replace(".", "_")
                                .replace("#", "_")
                                .replace("$", "_")
                                .replace("[", "_")
                                .replace("]", "_")
                                .replace("/", "_")
                            val isWordCompleted = uiState.completedWords.contains(safeKey) || uiState.isCorrect == true

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isWordCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF8E1))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isWordCompleted) "COMPLETADO" else "PENDIENTE",
                                    color = if (isWordCompleted) Color(0xFF2E7D32) else Color(0xFFF57F17),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Spanish Word (Reference)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "EN ESPAÑOL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = word.espanol,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Native Word (Quechua/Aimara) - Centerpiece
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "EN ${uiState.language.uppercase()}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val targetWordText = when (uiState.language.lowercase()) {
                                    "quechua" -> word.quechua
                                    "aimara" -> word.aimara
                                    else -> word.espanol
                                }
                                Text(
                                    text = targetWordText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                // Button to speak native word
                                IconButton(
                                    onClick = { if (!uiState.isAnalyzing) viewModel.speakCurrentWord() },
                                    enabled = !uiState.isAnalyzing,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (uiState.isAnalyzing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Escuchar pronunciación",
                                        tint = if (uiState.isAnalyzing) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Microphone & Recording feedback
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (uiState.isListening) {
                                    // Pulse animation for listening state
                                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                                    val pulseScale by infiniteTransition.animateFloat(
                                        initialValue = 1.0f,
                                        targetValue = 1.3f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(800, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "scale"
                                    )

                                    IconButton(
                                        onClick = { viewModel.stopRecording() },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = Color.Red
                                        ),
                                        modifier = Modifier
                                            .size(72.dp)
                                            .scale(pulseScale)
                                            .clip(CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Grabando",
                                            tint = Color.White,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Escuchando...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    IconButton(
                                        onClick = {
                                            if (!uiState.isAnalyzing) {
                                                if (hasMicPermission) {
                                                    viewModel.startRecording()
                                                } else {
                                                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                                                }
                                            }
                                        },
                                        enabled = !uiState.isAnalyzing,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = if (uiState.isAnalyzing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MicNone,
                                            contentDescription = "Pronunciar palabra",
                                            tint = if (uiState.isAnalyzing) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (uiState.isAnalyzing) "Analizando..." else "Pulsa para hablar",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Localized loading card
                    if (uiState.isAnalyzing) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Analizando tu pronunciación...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Pronunciation feedback panel
                    if (!uiState.isAnalyzing && (uiState.sttResult != null || uiState.feedback != null)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (uiState.isCorrect) {
                                    true -> Color(0xFFE8F5E9) // Light Green
                                    false -> Color(0xFFFFEBEE) // Light Red
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                uiState.sttResult?.let { result ->
                                    Text(
                                        text = "Escuché: \"$result\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                uiState.feedback?.let { feedback ->
                                    Text(
                                        text = feedback,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when (uiState.isCorrect) {
                                            true -> Color(0xFF2E7D32)
                                            false -> Color(0xFFC62828)
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    // Real "Intentar de nuevo" button inside the feedback panel for pronunciation failure
                                    if (uiState.isCorrect == false) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { viewModel.resetEvaluation() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Intentar de nuevo", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                     // Navigation / Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            TextButton(
                                onClick = { viewModel.deselectTema() },
                                enabled = !uiState.isAnalyzing
                            ) {
                                Text("Volver")
                            }
                            if (currentWordIndex > 0) {
                                TextButton(
                                    onClick = { viewModel.previousWord() },
                                    enabled = !uiState.isAnalyzing
                                ) {
                                    Text("Anterior")
                                }
                            }
                        }

                        val currentWordEspanolVal = word.espanol
                        val safeKeyVal = currentWordEspanolVal
                            .replace(".", "_")
                            .replace("#", "_")
                            .replace("$", "_")
                            .replace("[", "_")
                            .replace("]", "_")
                            .replace("/", "_")
                        val isWordCompletedVal = uiState.completedWords.contains(safeKeyVal) || uiState.isCorrect == true

                        Row {
                            Button(
                                onClick = {
                                    if (isWordCompletedVal && uiState.currentWordIndex >= words.size - 1) {
                                        viewModel.deselectTema()
                                    } else {
                                        viewModel.nextWord()
                                    }
                                },
                                enabled = !uiState.isAnalyzing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isWordCompletedVal) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (isWordCompletedVal) {
                                        if (currentWordIndex >= words.size - 1) "Finalizar" else "Siguiente"
                                    } else "Saltar"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
