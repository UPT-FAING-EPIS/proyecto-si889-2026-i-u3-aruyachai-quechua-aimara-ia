package com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.ChatMessage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.MessageRole

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val role: MessageRole,
    val timestamp: Long
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    content = content,
    role = role,
    timestamp = timestamp
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    id = id,
    content = content,
    role = role,
    timestamp = timestamp
)
