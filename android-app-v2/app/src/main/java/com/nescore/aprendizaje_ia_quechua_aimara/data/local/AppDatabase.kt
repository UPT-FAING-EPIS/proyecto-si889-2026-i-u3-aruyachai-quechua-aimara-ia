package com.nescore.aprendizaje_ia_quechua_aimara.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.ChatDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.TemaDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.WordleDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.PalabraDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.ChatMessageEntity
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.TemaEntity
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.WordleWordEntity
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.PalabraEntity

@Database(
    entities = [
        TemaEntity::class,
        WordleWordEntity::class,
        ChatMessageEntity::class,
        PalabraEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun temaDao(): TemaDao
    abstract fun wordleDao(): WordleDao
    abstract fun chatDao(): ChatDao
    abstract fun palabraDao(): PalabraDao
}
