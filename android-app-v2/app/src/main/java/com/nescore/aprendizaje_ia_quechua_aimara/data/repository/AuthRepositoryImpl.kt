package com.nescore.aprendizaje_ia_quechua_aimara.data.repository

import com.nescore.aprendizaje_ia_quechua_aimara.data.datasource.FirebaseAuthDataSource
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.User
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    override val currentUser: Flow<User?> = dataSource.currentUser

    override suspend fun loginWithGoogle(): Result<Unit> = dataSource.signInWithGoogle()

    override suspend fun loginAnonymously(): Result<Unit> = dataSource.signInAnonymously()

    override suspend fun logout(): Result<Unit> = dataSource.signOut()

    override fun isUserAnonymous(): Boolean = dataSource.isUserAnonymous()
}
