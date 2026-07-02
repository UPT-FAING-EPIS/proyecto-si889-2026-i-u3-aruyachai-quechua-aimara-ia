package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TemasScreen: Pantalla que actúa como el menú principal de categorías de aprendizaje.
 * 
 * Presenta una lista de temas disponibles en la aplicación. Al seleccionar un tema, 
 * se dispara un evento de navegación hacia la pantalla de detalle correspondiente.
 * 
 * @param onTemaClick Callback de tipo (String) -> Unit que se invoca al pulsar un tema, 
 * pasando el nombre del tema seleccionado para la navegación.
 */
@Composable
fun TemasScreen(onTemaClick: (String) -> Unit) {
    // Definición estática de las categorías disponibles. 
    // En una arquitectura más avanzada, estos podrían provenir de un ViewModel o Firestore.
    val temas = listOf("Saludos", "Números", "Familia", "Colores", "Cuerpo Humano", "Animales")

    // LazyColumn: Componente optimizado para renderizar listas de elementos de forma eficiente.
    // A diferencia de un Column tradicional con scroll, LazyColumn solo compone y dibuja 
    // los elementos que son visibles en la pantalla, ahorrando memoria y CPU.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp), // Margen interno de la lista completa.
        verticalArrangement = Arrangement.spacedBy(8.dp) // Separación uniforme entre tarjetas.
    ) {
        // El DSL 'items' recorre la colección y genera un componente para cada entrada.
        items(temas) { tema ->
            // Card: Contenedor con elevación y bordes redondeados siguiendo Material Design 3.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTemaClick(tema) }, // Gestiona la interacción del usuario.
                shape = MaterialTheme.shapes.medium
            ) {
                // ListItem: Componente especializado para filas de listas.
                // Facilita la organización de textos principales, secundarios e iconos/etiquetas.
                ListItem(
                    headlineContent = { 
                        Text(
                            text = tema, 
                            style = MaterialTheme.typography.titleMedium 
                        ) 
                    },
                    supportingContent = { 
                        Text(
                            text = "Pulsa para aprender", 
                            style = MaterialTheme.typography.bodySmall 
                        ) 
                    },
                    trailingContent = { 
                        // Etiqueta visual de acción a la derecha.
                        Text(
                            text = "Explorar", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = MaterialTheme.colorScheme.primary 
                        )
                    }
                )
            }
        }
    }
}
