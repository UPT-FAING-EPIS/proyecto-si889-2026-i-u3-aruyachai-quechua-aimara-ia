package com.nescore.aprendizaje_ia_quechua_aimara.data.model

/**
 * Modelo de datos para una palabra del juego Wordle.
 * Representa la estructura que se encuentra en Firestore.
 */
data class WordleWord(
    val id: String = "",
    val palabra_objetivo: String = "", // La palabra que el usuario debe adivinar (ej: "ALLQU")
    val traduccion_es: String = "",   // Traducción al español (ej: "Perro")
    val quechua: String = "",         // Palabra en Quechua
    val aimara: String = "",          // Palabra en Aimara
    val categoria: String = "",       // Ej: "animales", "colores"
    val dificultad: String = "easy",  // easy, medium, hard
    val pista: String = "",           // Pista inicial opcional
    val intentos_max: Int = 6         // Límite de intentos
)
