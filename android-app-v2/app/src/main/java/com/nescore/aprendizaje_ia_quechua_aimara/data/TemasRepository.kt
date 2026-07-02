package com.nescore.aprendizaje_ia_quechua_aimara.data

import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.Normalizer

/**
 * Repositorio encargado de obtener los datos de los temas desde Firebase Firestore.
 */
class TemasRepository {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Obtiene la lista de palabras/frases de un tema específico.
     * @param nombreTema El nombre del tema (ej. "Saludos", "Números")
     */
    suspend fun getPalabrasPorTema(nombreTema: String): List<Palabra> {
        return try {
            // Normalizamos el nombre del tema para que coincida con Firestore
            val idDocumento = normalizarId(nombreTema)

            val snapshot = firestore.collection("temas")
                .document(idDocumento)
                .collection("items")
                .orderBy("orden", Query.Direction.ASCENDING) // Ordenamos por el campo 'orden' de menor a mayor
                .get()
                .await()
            
            snapshot.toObjects(Palabra::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Función auxiliar para convertir "Números" en "numeros", "Saludos" en "saludos", etc.
     */
    private fun normalizarId(texto: String): String {
        val temp = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        return temp.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .replace(" ", "")
    }
}
