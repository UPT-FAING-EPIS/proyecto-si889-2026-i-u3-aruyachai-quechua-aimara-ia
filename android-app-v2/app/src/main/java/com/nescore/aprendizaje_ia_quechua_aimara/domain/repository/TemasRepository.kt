package com.nescore.aprendizaje_ia_quechua_aimara.domain.repository

import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema
import kotlinx.coroutines.flow.Flow

interface TemasRepository {
    suspend fun getTemas(): Result<List<Tema>>
    suspend fun getPalabrasPorTema(nombreTema: String): Result<List<Palabra>>
    fun getAllTemasLocal(): Flow<List<Tema>>
    suspend fun saveTemasLocal(temas: List<Tema>)
}
