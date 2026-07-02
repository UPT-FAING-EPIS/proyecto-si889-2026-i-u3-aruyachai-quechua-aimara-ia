package com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema

@Entity(tableName = "temas")
data class TemaEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val imagenUrl: String?
)

fun TemaEntity.toDomain() = Tema(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    imagenUrl = imagenUrl
)

fun Tema.toEntity() = TemaEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    imagenUrl = imagenUrl
)
