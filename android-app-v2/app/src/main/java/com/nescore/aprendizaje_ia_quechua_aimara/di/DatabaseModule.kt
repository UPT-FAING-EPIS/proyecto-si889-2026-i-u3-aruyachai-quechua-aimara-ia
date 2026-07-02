package com.nescore.aprendizaje_ia_quechua_aimara.di

import android.content.Context
import androidx.room.Room
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.AppDatabase
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.ChatDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.TemaDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.WordleDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.PalabraDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "aprendizaje_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideTemaDao(database: AppDatabase): TemaDao = database.temaDao()

    @Provides
    fun provideWordleDao(database: AppDatabase): WordleDao = database.wordleDao()

    @Provides
    fun provideChatDao(database: AppDatabase): ChatDao = database.chatDao()

    @Provides
    fun providePalabraDao(database: AppDatabase): PalabraDao = database.palabraDao()
}
