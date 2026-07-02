package com.nescore.aprendizaje_ia_quechua_aimara.data.model

import androidx.annotation.Keep

/**
 * Data class que representa una palabra o frase en los tres idiomas.
 * Los nombres de los campos coinciden con los de Firestore para facilitar el mapeo.
 */
@Keep
data class Palabra(
    val espanol: String = "",
    val quechua: String = "",
    val aimara: String = ""
)
