package com.nescore.aprendizaje_ia_quechua_aimara.di

import com.nescore.aprendizaje_ia_quechua_aimara.data.repository.WordleRepositoryImpl
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.WordleRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WordleModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideWordleRepository(
        repositoryImpl: WordleRepositoryImpl
    ): WordleRepository = repositoryImpl
}
