package com.nescore.aprendizaje_ia_quechua_aimara.domain.model

import androidx.annotation.Keep

@Keep
data class Tema(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagenUrl: String? = null
)
