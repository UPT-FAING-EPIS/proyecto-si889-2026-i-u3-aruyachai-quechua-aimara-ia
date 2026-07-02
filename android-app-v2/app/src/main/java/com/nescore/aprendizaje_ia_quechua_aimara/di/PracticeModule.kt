package com.nescore.aprendizaje_ia_quechua_aimara.di

import com.nescore.aprendizaje_ia_quechua_aimara.data.repository.PracticeRepositoryImpl
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.PracticeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PracticeModule {

    @Binds
    @Singleton
    abstract fun bindPracticeRepository(
        practiceRepositoryImpl: PracticeRepositoryImpl
    ): PracticeRepository
}
