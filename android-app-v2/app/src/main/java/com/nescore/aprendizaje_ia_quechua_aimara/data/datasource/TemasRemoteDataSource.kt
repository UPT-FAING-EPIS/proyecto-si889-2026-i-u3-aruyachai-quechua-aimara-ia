package com.nescore.aprendizaje_ia_quechua_aimara.data.datasource

import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.Normalizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemasRemoteDataSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    suspend fun getTemas(): Result<List<Tema>> {
        return try {
            val snapshot = database.getReference("temas").get().await()
            val temasList = snapshot.children.mapNotNull { childSnapshot ->
                val id = childSnapshot.key ?: return@mapNotNull null
                val nombre = when (id) {
                    "animales" -> "Animales"
                    "colores" -> "Colores"
                    "cuerpoHumano" -> "Cuerpo Humano"
                    "numeros" -> "Números"
                    "saludos" -> "Saludos"
                    "alimentos" -> "Alimentos"
                    "naturaleza" -> "Naturaleza"
                    "hogar" -> "Hogar"
                    "profesiones" -> "Profesiones"
                    "ropa" -> "Ropa"
                    else -> id.replaceFirstChar { it.uppercase() }
                }
                val descripcion = when (id) {
                    "animales" -> "Animales de la región"
                    "colores" -> "Los colores de la naturaleza"
                    "cuerpoHumano" -> "Partes del cuerpo"
                    "numeros" -> "Cuenta en Quechua y Aimara"
                    "saludos" -> "Aprende a saludar"
                    "alimentos" -> "Vocabulario de alimentos"
                    "naturaleza" -> "Elementos de la naturaleza"
                    "hogar" -> "Cosas del hogar"
                    "profesiones" -> "Profesiones y oficios"
                    "ropa" -> "Prendas de vestir"
                    else -> "Aprende sobre $nombre"
                }
                Tema(id = id, nombre = nombre, descripcion = descripcion)
            }
            Result.success(temasList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPalabrasPorTema(nombreTema: String): Result<List<Palabra>> {
        return try {
            val idDocumento = normalizarId(nombreTema)
            val snapshot = database.getReference("temas")
                .child(idDocumento)
                .child("items")
                .get()
                .await()
            
            val palabrasList = snapshot.children.mapNotNull { itemSnapshot ->
                itemSnapshot.getValue(Palabra::class.java)
            }
            Result.success(palabrasList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun normalizarId(texto: String): String {
        val temp = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        val normalized = temp.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .replace(" ", "")
        return if (normalized == "cuerpohumano") "cuerpoHumano" else normalized
    }
}
