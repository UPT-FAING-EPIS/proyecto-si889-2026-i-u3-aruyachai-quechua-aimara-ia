package com.nescore.aprendizaje_ia_quechua_aimara.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object LeaderboardHelper {
    suspend fun updateLeaderboard(uid: String) {
        try {
            val database = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser ?: return
            
            // 1. Obtener total de palabras completadas
            var wordsCompleted = 0
            val guiaSnapshot = database.getReference("progreso")
                .child(uid)
                .child("guia")
                .get()
                .await()
            
            if (guiaSnapshot.exists()) {
                // Estructura: progreso/{uid}/guia/{idioma}/{temaId}/{palabra}
                guiaSnapshot.children.forEach { langSnap ->
                    langSnap.children.forEach { temaSnap ->
                        wordsCompleted += temaSnap.childrenCount.toInt()
                    }
                }
            }
            
            // 2. Obtener total de exámenes aprobados
            var examsPassed = 0
            val practicasSnapshot = database.getReference("progreso")
                .child(uid)
                .child("practicas")
                .get()
                .await()
            
            if (practicasSnapshot.exists()) {
                // Estructura: progreso/{uid}/practicas/{idioma}/{nivel}/{examenTitle}
                practicasSnapshot.children.forEach { langSnap ->
                    langSnap.children.forEach { levelSnap ->
                        examsPassed += levelSnap.childrenCount.toInt()
                    }
                }
            }
            
            // 3. Calcular Score total
            val score = (wordsCompleted * 10) + (examsPassed * 50)
            
            // 4. Obtener nombre y foto del usuario
            val emailPrefix = user.email?.substringBefore("@") ?: "Estudiante"
            val displayName = when {
                !user.displayName.isNullOrBlank() -> user.displayName!!
                user.isAnonymous -> "Invitado_${uid.take(4)}"
                else -> emailPrefix
            }
            val photoUrl = user.photoUrl?.toString()
            
            // 5. Guardar en el Leaderboard global
            val leaderRef = database.getReference("leaderboard").child(uid)
            val data = hashMapOf(
                "uid" to uid,
                "displayName" to displayName,
                "photoUrl" to photoUrl,
                "wordsCompleted" to wordsCompleted,
                "examsPassed" to examsPassed,
                "score" to score
            )
            leaderRef.setValue(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
