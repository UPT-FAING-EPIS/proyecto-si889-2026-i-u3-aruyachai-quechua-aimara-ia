package com.nescore.aprendizaje_ia_quechua_aimara.domain.model

import androidx.annotation.Keep

@Keep
data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean
)
