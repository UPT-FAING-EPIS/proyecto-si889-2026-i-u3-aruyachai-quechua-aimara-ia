package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.MessageRole
import com.nescore.aprendizaje_ia_quechua_aimara.ui.chat.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startRecording()
        }
    }

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    // Usamos background para que si hay algún gap por el teclado, sea del color del tema
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding() // Maneja el teclado
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (viewModel.messages.isEmpty()) {
                item {
                    AssistantTriBubble(
                        spanish = "¡Hola! Soy tu tutor de lenguas andinas.",
                        quechua = "¡Imaynalla! Qichwa simita yachaq tumpam kani.",
                        aymara = "¡Kamisaraki! Nayax aymar yatiqir yanapiriwa.",
                        onSpeak = { text, lang -> viewModel.speak(text, lang) }
                    )
                }
            }

            items(viewModel.messages) { message ->
                val triResponse = message.parseTriResponse()
                if (message.role == MessageRole.USER) {
                    UserBubble(message.content)
                } else if (triResponse != null) {
                    AssistantTriBubble(
                        spanish = triResponse.first,
                        quechua = triResponse.second,
                        aymara = triResponse.third,
                        onSpeak = { text, lang -> viewModel.speak(text, lang) }
                    )
                } else {
                    SimpleAssistantBubble(message.content, onSpeak = { text, lang -> viewModel.speak(text, lang) })
                }
            }

            if (viewModel.isLoading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(start = 8.dp))
                }
            }
        }

        if (viewModel.sttError != null) {
            Text(
                text = viewModel.sttError!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        ChatInputArea(
            text = viewModel.inputText,
            onValueChange = { viewModel.onInputChange(it) },
            onSend = { viewModel.sendMessage() },
            onMicClick = {
                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    if (viewModel.isRecording) viewModel.stopRecording()
                    else viewModel.startRecording()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            isRecording = viewModel.isRecording,
            enabled = !viewModel.isLoading
        )
    }
}

@Composable
fun UserBubble(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(text, color = Color.White, modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
fun AssistantTriBubble(
    spanish: String,
    quechua: String,
    aymara: String,
    onSpeak: (String, String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
        modifier = Modifier.widthIn(max = 320.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            LanguageRow("Español", spanish, "es", onSpeak)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            LanguageRow("Quechua", quechua, "quechua", onSpeak)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            LanguageRow("Aimara", aymara, "aimara", onSpeak)
        }
    }
}

@Composable
fun LanguageRow(label: String, text: String, langCode: String, onSpeak: (String, String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onSpeak(text, langCode) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar", modifier = Modifier.size(18.dp))
            }
        }
        Text(text, fontSize = 15.sp)
    }
}

@Composable
fun SimpleAssistantBubble(text: String, onSpeak: (String, String) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
        modifier = Modifier.widthIn(max = 280.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, modifier = Modifier.weight(1f), fontSize = 15.sp)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onSpeak(text, "es") }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun ChatInputArea(
    text: String, 
    onValueChange: (String) -> Unit, 
    onSend: () -> Unit, 
    onMicClick: () -> Unit,
    isRecording: Boolean,
    enabled: Boolean
) {
    Surface(
        tonalElevation = 2.dp, 
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface // Asegura que el fondo del input no sea transparente
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .navigationBarsPadding(), // Asegura padding correcto con la barra de sistema
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMicClick,
                enabled = enabled
            ) {
                if (isRecording) {
                    val infiniteTransition = rememberInfiniteTransition(label = "mic")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )
                    Icon(
                        Icons.Default.Stop, 
                        contentDescription = "Detener", 
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size((24 * scale).dp)
                    )
                } else {
                    Icon(
                        Icons.Default.Mic, 
                        contentDescription = "Voz", 
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            OutlinedTextField(
                value = text,
                onValueChange = onValueChange,
                placeholder = { 
                    Text(if (isRecording) "Escuchando..." else "Escribe aquí...") 
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                enabled = enabled && !isRecording,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            IconButton(
                onClick = onSend, 
                enabled = enabled && text.isNotBlank() && !isRecording
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send, 
                    contentDescription = "Enviar", 
                    tint = if (enabled && text.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}
