package com.nescore.aprendizaje_ia_quechua_aimara.domain.repository

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun loginWithGoogle(): Result<Unit>
    suspend fun loginAnonymously(): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun isUserAnonymous(): Boolean
}
