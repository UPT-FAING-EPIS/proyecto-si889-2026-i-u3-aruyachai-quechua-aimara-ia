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
import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import java.util.Locale

/**
 * DetalleTemaScreen: Pantalla encargada de mostrar el vocabulario específico de un tema.
 * Implementa un buscador en tiempo real y soporte para síntesis de voz (Text-to-Speech) nativa.
 * 
 * Sigue el patrón MVVM donde el estado es manejado por [TemasViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTemaScreen(
    temaNombre: String,
    viewModel: TemasViewModel,
    onBack: () -> Unit
) {
    // Suscripción a los flujos de estado del ViewModel. 
    // Al usar collectAsState, Compose escucha cambios en el StateFlow y dispara la recomposición.
    val query by viewModel.searchQuery.collectAsState()
    val palabrasFiltradas by viewModel.palabrasFiltradas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val context = LocalContext.current
    
    // Motor TTS nativo: Se utiliza 'remember' para que la instancia persista durante recomposiciones.
    // El objeto se inicializa una sola vez al entrar en la composición.
    val tts = remember {
        TextToSpeech(context) { status -> 
            // Callback de inicialización: Aquí se podría configurar el idioma o manejar errores.
        }
    }

    // LaunchedEffect: Disparador de efectos secundarios. 
    // Se ejecuta al inicio y cada vez que 'temaNombre' cambia, cargando los datos desde Firestore.
    LaunchedEffect(temaNombre) {
        viewModel.cargarPalabras(temaNombre)
    }

    Scaffold(
        topBar = {
            // Contenedor de la barra superior que incluye el título y la barra de búsqueda.
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
                
                // Barra de búsqueda integrada: Notifica al ViewModel cada vez que el valor cambia,
                // disparando el filtrado reactivo de la lista.
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
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
        // Lógica de visualización condicional basada en el estado de carga y resultados.
        if (isLoading) {
            // Estado de carga: Muestra un spinner mientras los datos bajan de Firestore.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Validación de resultados vacíos tras una búsqueda activa.
            if (palabrasFiltradas.isEmpty() && query.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron resultados para \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // LazyColumn: Componente optimizado para listas largas, renderiza solo lo visible.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(palabrasFiltradas) { palabra ->
                        // Componente de tarjeta individual para cada palabra.
                        PalabraCardRediseñada(
                            palabra = palabra,
                            onSpeak = { texto ->
                                // Reproducción de audio mediante el motor TTS.
                                tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // DisposableEffect: Maneja la limpieza de recursos.
    // Se ejecuta al salir de la pantalla, deteniendo y cerrando el motor TTS 
    // para evitar fugas de memoria (Memory Leaks).
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
}

/**
 * PalabraCardRediseñada: Componente visual de tarjeta que presenta la palabra en español
 * y sus traducciones a Quechua y Aimara con soporte de audio.
 */
@Composable
fun PalabraCardRediseñada(palabra: Palabra, onSpeak: (String) -> Unit) {
    // Estados locales para gestionar el feedback visual (animaciones) al reproducir audio.
    var isPlayingQuechua by remember { mutableStateOf(false) }
    var isPlayingAimara by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Término en Español (Título de la tarjeta).
            Text(
                text = palabra.espanol,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Fila interactiva para el idioma Quechua.
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

            // Fila interactiva para el idioma Aimara.
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

/**
 * IdiomaActionRow: Fila personalizada que contiene el texto traducido y el botón de audio.
 * Implementa una animación de color suave al ser pulsado.
 */
@Composable
fun IdiomaActionRow(
    idioma: String,
    texto: String,
    color: Color,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onAnimationFinished: () -> Unit
) {
    // Animación de color de fondo: Reacciona al estado 'isPlaying'.
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
                // Etiqueta del idioma (QUECHUA / AIMARA).
                Text(
                    text = idioma.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Traducción de la palabra.
                Text(
                    text = texto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Botón visual con icono de volumen indicando la acción de oír.
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
