package com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity

import androidx.room.Entity
import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra

@Entity(tableName = "palabras", primaryKeys = ["temaId", "espanol"])
data class PalabraEntity(
    val temaId: String,
    val espanol: String,
    val quechua: String,
    val aimara: String
)

fun PalabraEntity.toDomain() = Palabra(
    espanol = espanol,
    quechua = quechua,
    aimara = aimara
)

fun Palabra.toEntity(temaId: String) = PalabraEntity(
    temaId = temaId,
    espanol = espanol,
    quechua = quechua,
    aimara = aimara
)
