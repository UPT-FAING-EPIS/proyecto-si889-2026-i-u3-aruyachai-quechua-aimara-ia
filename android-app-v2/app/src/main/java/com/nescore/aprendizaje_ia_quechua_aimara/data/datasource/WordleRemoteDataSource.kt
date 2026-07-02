package com.nescore.aprendizaje_ia_quechua_aimara.data.datasource

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleWord
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordleRemoteDataSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    suspend fun getWordleWords(): Result<List<WordleWord>> {
        return try {
            val snapshot = database.getReference("wordle").get().await()
            val words = snapshot.children.mapNotNull { childSnapshot ->
                val id = childSnapshot.key ?: return@mapNotNull null
                val aimara = childSnapshot.child("aimara").getValue(String::class.java) ?: ""
                val espanol = childSnapshot.child("espanol").getValue(String::class.java) ?: ""
                val quechua = childSnapshot.child("quechua").getValue(String::class.java) ?: ""
                
                val categoria = getCategoryForWord(id, espanol)
                WordleWord(
                    id = id,
                    quechua = quechua,
                    aimara = aimara,
                    espanol = espanol,
                    categoria = categoria,
                    descripcion = "Palabra para jugar en Wordle",
                    intentos_max = 6
                )
            }
            Result.success(words)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCategoryForWord(id: String, espanol: String): String {
        return when (id) {
            "w1", "w2", "w3", "w4", "w5", "w13", "w14" -> "animales"
            "w6", "w7", "w8" -> "colores"
            "w9", "w10", "w11", "w12" -> "cuerpohumano"
            "w15", "w16", "w17", "w18" -> "numeros"
            "w19", "w20" -> "saludos"
            else -> {
                val espLower = espanol.lowercase()
                if (espLower in listOf("perro", "gato", "vaca", "cóndor", "zorro", "pez", "mono", "llama", "alpaca", "vicuña", "oveja", "ratón", "caballo", "gallina", "pájaro", "mariposa", "cerdo", "pato", "conejo")) "animales"
                else if (espLower in listOf("rojo", "amarillo", "morado", "blanco", "negro", "azul", "verde", "gris", "naranja", "celeste", "marrón", "rosado", "plateado", "dorado", "turquesa", "beige", "violeta", "vino")) "colores"
                else if (espLower in listOf("cabeza", "boca", "mano", "pie", "cara", "ojo", "oreja", "nariz", "cabello", "cuello", "pecho", "espalda", "brazo", "pierna", "dedo", "diente", "lengua", "corazón", "sangre", "hueso")) "cuerpohumano"
                else if (espLower in listOf("uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "veinte", "treinta", "cuarenta", "cincuenta", "cien")) "numeros"
                else "saludos"
            }
        }
    }
}
