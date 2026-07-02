package com.nescore.aprendizaje_ia_quechua_aimara.di

import android.content.Context
import com.nescore.aprendizaje_ia_quechua_aimara.data.repository.FirebaseChatRepository
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.ChatRepository
import com.nescore.aprendizaje_ia_quechua_aimara.ui.chat.TTSManager
import com.nescore.aprendizaje_ia_quechua_aimara.ui.chat.SpeechToTextManager
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return FirebaseFunctions.getInstance(FirebaseApp.getInstance(), "us-central1")
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        repository: FirebaseChatRepository
    ): ChatRepository = repository

    @Provides
    @Singleton
    fun provideTTSManager(@ApplicationContext context: Context): TTSManager = TTSManager(context)

    @Provides
    @Singleton
    fun provideSpeechToTextManager(@ApplicationContext context: Context): SpeechToTextManager = SpeechToTextManager(context)
}
