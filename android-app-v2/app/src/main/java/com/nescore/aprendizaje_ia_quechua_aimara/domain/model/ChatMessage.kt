package com.nescore.aprendizaje_ia_quechua_aimara.domain.model

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun parseTriResponse(): Triple<String, String, String>? {
        return try {
            val spanish = content.substringAfter("Español:").substringBefore("Quechua:").trim()
            val quechua = content.substringAfter("Quechua:").substringBefore("Aimara:").trim()
            val aymara = content.substringAfter("Aimara:").trim()
            
            if (content.contains("Español:") && content.contains("Quechua:") && content.contains("Aimara:")) {
                Triple(spanish, quechua, aymara)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

@Keep
enum class MessageRole {
    USER, ASSISTANT
}

@Keep
data class AIResponse(
    val spanish: String,
    val quechua: String,
    val aymara: String
)
