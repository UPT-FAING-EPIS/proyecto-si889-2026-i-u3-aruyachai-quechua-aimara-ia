package com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.TemaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemaDao {
    @Query("SELECT * FROM temas")
    fun getAllTemas(): Flow<List<TemaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemas(temas: List<TemaEntity>)

    @Query("DELETE FROM temas")
    suspend fun deleteAllTemas()
}
