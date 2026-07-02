package com.nescore.aprendizaje_ia_quechua_aimara.data

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

/**
 * IAHelpRepository: Gestiona la comunicación con modelos de IA (OpenAI) de forma segura.
 * 
 * Esta clase utiliza Firebase Cloud Functions para delegar el procesamiento de IA 
 * al servidor. Esto es crucial por dos razones técnicas:
 * 1. Seguridad: Evita exponer la API Key de OpenAI dentro del código cliente de la app.
 * 2. Escalabilidad: Permite actualizar la lógica de la IA sin necesidad de lanzar una nueva versión de la app.
 */
class IAHelpRepository {
    private val functions = FirebaseFunctions.getInstance()

    /**
     * Solicita dinámicamente una pista contextual a la IA.
     * 
     * @param palabra La palabra secreta que el usuario intenta adivinar.
     * @param idioma El contexto lingüístico actual (Quechua/Aimara).
     * @param categoria El dominio del conocimiento (ej. Animales).
     * @return Result<String> Un objeto envoltorio que contiene la respuesta de la IA o el error capturado.
     */
    suspend fun getWordleHint(palabra: String, idioma: String, categoria: String): Result<String> {
        return try {
            // Empaquetado de parámetros para la Cloud Function.
            val data = hashMapOf(
                "action" to "wordle_hint",
                "word" to palabra,
                "language" to idioma,
                "category" to categoria
            )

            // Invocación remota de la función "openai_assistant".
            // Se utiliza .await() para suspender la ejecución de la corrutina sin bloquear el hilo UI.
            val result = functions
                .getHttpsCallable("openai_assistant")
                .call(data)
                .await()

            // Deserialización segura del resultado.
            val response = result.data as? Map<*, *>
            val text = response?.get("text") as? String ?: "No hay pistas disponibles."
            
            Result.success(text)
        } catch (e: Exception) {
            // Gestión de errores: Captura fallos de red, timeouts o errores de la función remota.
            Result.failure(e)
        }
    }
}
