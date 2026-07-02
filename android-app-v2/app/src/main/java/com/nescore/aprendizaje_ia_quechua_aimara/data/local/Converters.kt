package com.nescore.aprendizaje_ia_quechua_aimara.data.local

import androidx.room.TypeConverter
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.MessageRole

class Converters {
    @TypeConverter
    fun fromMessageRole(role: MessageRole): String {
        return role.name
    }

    @TypeConverter
    fun toMessageRole(name: String): MessageRole {
        return MessageRole.valueOf(name)
    }
}
