package com.nescore.aprendizaje_ia_quechua_aimara.di

import com.nescore.aprendizaje_ia_quechua_aimara.data.datasource.FirebaseAuthDataSource
import com.nescore.aprendizaje_ia_quechua_aimara.data.repository.AuthRepositoryImpl
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource
    ): AuthRepository = AuthRepositoryImpl(dataSource)
}
