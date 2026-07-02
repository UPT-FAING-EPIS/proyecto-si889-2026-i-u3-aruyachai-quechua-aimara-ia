package com.nescore.aprendizaje_ia_quechua_aimara.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int,
    achievementName: String,
    shareMessage: String,
    onRestart: () -> Unit,
    onBackToHome: () -> Unit,
    onShare: (String) -> Unit
) {
    val percentage = (score.toFloat() / totalQuestions * 100).toInt()
    
    val rank = when {
        percentage == 100 -> "Maestro Andino"
        percentage >= 80 -> "Aprendiz Avanzado"
        else -> "Explorador Lingüístico"
    }

    // Fondo oscuro profundo para toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark Background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFFFFD700) // Gold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Examen Completado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu puntaje: $score / $totalQuestions ($percentage%)",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFB0BEC5)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de Logro con estilo oscuro
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Logro Desbloqueado",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = achievementName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Rango: $rank",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFF81C784)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { onShare(shareMessage) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Compartir Logro", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Text("Reintentar Examen", color = Color.White)
        }

        TextButton(onClick = onBackToHome) {
            Text("Volver al Inicio", color = Color(0xFF90A4AE))
        }
    }
}
