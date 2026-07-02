package com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.WordleWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordleDao {
    @Query("SELECT * FROM wordle_words")
    fun getAllWords(): Flow<List<WordleWordEntity>>

    @Query("SELECT * FROM wordle_words WHERE categoria = :category ORDER BY RANDOM() LIMIT 10")
    suspend fun getRandomWordsByCategory(category: String): List<WordleWordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordleWordEntity>)

    @Query("DELETE FROM wordle_words")
    suspend fun deleteAllWords()
}
