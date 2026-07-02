package com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.WordleWord

@Entity(tableName = "wordle_words")
data class WordleWordEntity(
    @PrimaryKey val id: String,
    val quechua: String,
    val aimara: String,
    val espanol: String,
    val categoria: String,
    val descripcion: String,
    val intentos_max: Int
)

fun WordleWordEntity.toDomain() = WordleWord(
    id = id,
    quechua = quechua,
    aimara = aimara,
    espanol = espanol,
    categoria = categoria,
    descripcion = descripcion,
    intentos_max = intentos_max
)

fun WordleWord.toEntity() = WordleWordEntity(
    id = id,
    quechua = quechua,
    aimara = aimara,
    espanol = espanol,
    categoria = categoria,
    descripcion = descripcion,
    intentos_max = intentos_max
)
