package com.nescore.aprendizaje_ia_quechua_aimara.data

import com.nescore.aprendizaje_ia_quechua_aimara.data.model.WordleWord
import com.nescore.aprendizaje_ia_quechua_aimara.ui.home.WordleLanguage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * WordleRepository: Gestiona la obtención de palabras para el juego desde Firestore.
 * Refactorizado para soportar filtrado por idioma (Quechua/Aimara).
 */
class WordleRepository {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Obtiene una palabra aleatoria filtrada por categoría e idioma.
     */
    suspend fun getRandomWord(language: WordleLanguage, categoria: String): WordleWord? {
        return try {
            val snapshot = firestore.collection("wordle_words")
                .whereEqualTo("categoria", categoria.lowercase())
                .get()
                .await()
            
            val words = snapshot.toObjects(WordleWord::class.java).filter {
                // Verificamos que la palabra no esté vacía para el idioma seleccionado
                if (language == WordleLanguage.QUECHUA) it.quechua.isNotBlank()
                else it.aimara.isNotBlank()
            }

            if (words.isNotEmpty()) words.random() else null
        } catch (e: Exception) {
            null
        }
    }
}
