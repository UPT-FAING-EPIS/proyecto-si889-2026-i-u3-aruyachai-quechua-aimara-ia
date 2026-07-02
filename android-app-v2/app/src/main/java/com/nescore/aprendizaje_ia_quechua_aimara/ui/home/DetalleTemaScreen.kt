package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTemaScreen(
    temaNombre: String,
    onBack: () -> Unit,
    viewModel: TemasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val palabrasFiltradas by viewModel.palabrasFiltradas.collectAsState()
    
    val context = LocalContext.current
    
    val tts = remember {
        TextToSpeech(context) { _ -> }
    }

    LaunchedEffect(temaNombre) {
        viewModel.onEvent(TemasUiEvent.LoadPalabras(temaNombre))
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = { 
                        Text(
                            text = temaNombre.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onEvent(TemasUiEvent.SearchQueryChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar palabra...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (palabrasFiltradas.isEmpty() && uiState.searchQuery.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron resultados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(palabrasFiltradas) { palabra ->
                        PalabraCardRediseñada(
                            palabra = palabra,
                            onSpeak = { texto ->
                                tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }
                }
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
}

@Composable
fun PalabraCardRediseñada(palabra: Palabra, onSpeak: (String) -> Unit) {
    var isPlayingQuechua by remember { mutableStateOf(false) }
    var isPlayingAimara by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = palabra.espanol,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            IdiomaActionRow(
                idioma = "Quechua",
                texto = palabra.quechua,
                color = MaterialTheme.colorScheme.primaryContainer,
                onPlay = {
                    onSpeak(palabra.quechua)
                    isPlayingQuechua = true
                },
                isPlaying = isPlayingQuechua,
                onAnimationFinished = { isPlayingQuechua = false }
            )

            Spacer(modifier = Modifier.height(12.dp))

            IdiomaActionRow(
                idioma = "Aimara",
                texto = palabra.aimara,
                color = MaterialTheme.colorScheme.secondaryContainer,
                onPlay = {
                    onSpeak(palabra.aimara)
                    isPlayingAimara = true
                },
                isPlaying = isPlayingAimara,
                onAnimationFinished = { isPlayingAimara = false }
            )
        }
    }
}

@Composable
fun IdiomaActionRow(
    idioma: String,
    texto: String,
    color: Color,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onAnimationFinished: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isPlaying) color.copy(alpha = 0.8f) else color.copy(alpha = 0.3f),
        label = "colorAnim",
        finishedListener = { if (isPlaying) onAnimationFinished() }
    )

    Surface(
        onClick = onPlay,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = idioma.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = texto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "OÍR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
