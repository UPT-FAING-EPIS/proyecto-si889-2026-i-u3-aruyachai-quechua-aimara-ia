package com.nescore.aprendizaje_ia_quechua_aimara.domain.repository

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getAIResponse(prompt: String): Result<String>
    suspend fun getAIAudioResponse(audioPath: String): Result<Map<String, String>>
    suspend fun assessPronunciation(audioPath: String, targetWord: String, language: String, translation: String): Result<Map<String, Any>>
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun saveMessage(message: ChatMessage)
    suspend fun saveMessages(messages: List<ChatMessage>)
    suspend fun clearChat()
}
