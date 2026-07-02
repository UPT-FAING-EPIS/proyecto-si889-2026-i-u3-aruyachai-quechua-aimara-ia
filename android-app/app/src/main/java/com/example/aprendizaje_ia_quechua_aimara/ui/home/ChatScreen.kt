package com.nescoreaprendizaje_ia_quechua_aimara.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * ChatScreen: Representa la interfaz de mensajería para interactuar con la IA.
 * Utiliza un diseño basado en una lista vertical (LazyColumn) para el historial 
 * y un área de entrada persistente en la parte inferior.
 */
@Composable
fun ChatScreen() {
    // Estado local para el texto que el usuario está escribiendo.
    // Se utiliza 'remember' para mantener el valor durante las recomposiciones.
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Historial de chat: Renderiza de forma eficiente los mensajes.
        // El peso (weight) 1f asegura que ocupe todo el espacio disponible dejando el input abajo.
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                // Mensaje de bienvenida inicial (Simulado).
                ChatBubble(message = "¡Imaynalla! ¿En qué puedo ayudarte hoy?", isUser = false)
            }
        }

        // Área de entrada: Contiene el campo de texto y el botón de acción.
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de texto optimizado para una sola línea.
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Escribe en Quechua o Aimara...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                // Acción de envío: Actualmente resetea el campo. 
                // Pendiente: Conectar con IAHelpRepository para procesar la consulta.
                IconButton(onClick = { text = "" }) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send, 
                        contentDescription = "Enviar", 
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * ChatBubble: Componente visual para representar un mensaje individual.
 * @param message Texto a mostrar.
 * @param isUser Booleano que define la alineación y el esquema de color (Derecha/Izquierda).
 */
@Composable
fun ChatBubble(message: String, isUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        // Alineación dinámica: El usuario a la derecha, la IA a la izquierda.
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            // Variación de color basada en el rol del emisor.
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 1.dp
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
