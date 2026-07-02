package com.nescore.aprendizaje_ia_quechua_aimara.data.repository

import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.ChatDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.toDomain
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.toEntity
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.ChatMessage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.ChatRepository
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseChatRepository @Inject constructor(
    private val functions: FirebaseFunctions,
    private val chatDao: ChatDao
) : ChatRepository {

    override suspend fun getAIResponse(prompt: String): Result<String> {
        return try {
            val data = hashMapOf("prompt" to prompt)
            val result = functions
                .getHttpsCallable("getOpenAIResponse")
                .call(data)
                .await()

            val responseData = result.data as Map<*, *>
            val text = responseData["response"] as String
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAIAudioResponse(audioPath: String): Result<Map<String, String>> {
        return try {
            val data = hashMapOf("audioPath" to audioPath)

            val result = functions
                .getHttpsCallable("processAudioMessage")
                .call(data)
                .await()

            val responseData = result.data as Map<*, *>
            val resultMap = mutableMapOf<String, String>()
            responseData.forEach { (key, value) ->
                if (key is String && value is String) {
                    resultMap[key] = value
                }
            }
            Result.success(resultMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMessages(): Flow<List<ChatMessage>> {
        return chatDao.getAllMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveMessage(message: ChatMessage) {
        chatDao.insertMessage(message.toEntity())
    }

    override suspend fun saveMessages(messages: List<ChatMessage>) {
        chatDao.insertMessages(messages.map { it.toEntity() })
    }

    override suspend fun clearChat() {
        chatDao.deleteAllMessages()
    }

    override suspend fun assessPronunciation(
        audioPath: String,
        targetWord: String,
        language: String,
        translation: String
    ): Result<Map<String, Any>> {
        return try {
            val data = hashMapOf(
                "audioPath" to audioPath,
                "targetWord" to targetWord,
                "language" to language,
                "translation" to translation
            )
            val result = functions
                .getHttpsCallable("assessPronunciation")
                .call(data)
                .await()

            val responseData = result.data as Map<*, *>
            val resultMap = mutableMapOf<String, Any>()
            responseData.forEach { (key, value) ->
                if (key is String && value != null) {
                    resultMap[key] = value
                }
            }
            Result.success(resultMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
