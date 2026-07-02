package com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.PalabraEntity

@Dao
interface PalabraDao {
    @Query("SELECT * FROM palabras WHERE temaId = :temaId")
    suspend fun getPalabrasByTema(temaId: String): List<PalabraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPalabras(palabras: List<PalabraEntity>)

    @Query("DELETE FROM palabras WHERE temaId = :temaId")
    suspend fun deletePalabrasByTema(temaId: String)
}
