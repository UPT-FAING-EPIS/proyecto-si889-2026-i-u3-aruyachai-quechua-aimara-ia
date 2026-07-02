package com.nescore.aprendizaje_ia_quechua_aimara.data.repository

import com.nescore.aprendizaje_ia_quechua_aimara.data.datasource.TemasRemoteDataSource
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.TemaDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.dao.PalabraDao
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.toDomain
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.toEntity
import com.nescore.aprendizaje_ia_quechua_aimara.data.local.entity.PalabraEntity
import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.TemasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.Normalizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemasRepositoryImpl @Inject constructor(
    private val remoteDataSource: TemasRemoteDataSource,
    private val localDataSource: TemaDao,
    private val localPalabraDao: PalabraDao
) : TemasRepository {

    override suspend fun getTemas(): Result<List<Tema>> {
        val result = remoteDataSource.getTemas()
        result.onSuccess { temas ->
            saveTemasLocal(temas)
        }
        return result
    }

    override suspend fun getPalabrasPorTema(nombreTema: String): Result<List<Palabra>> {
        val temaId = normalizarId(nombreTema)
        return try {
            val result = remoteDataSource.getPalabrasPorTema(nombreTema)
            result.onSuccess { palabras ->
                localPalabraDao.deletePalabrasByTema(temaId)
                localPalabraDao.insertPalabras(palabras.map { PalabraEntity(temaId, it.espanol, it.quechua, it.aimara) })
            }
            if (result.isSuccess) {
                result
            } else {
                val cached = localPalabraDao.getPalabrasByTema(temaId).map { Palabra(it.espanol, it.quechua, it.aimara) }
                if (cached.isNotEmpty()) {
                    Result.success(cached)
                } else {
                    result
                }
            }
        } catch (e: Exception) {
            val cached = localPalabraDao.getPalabrasByTema(temaId).map { Palabra(it.espanol, it.quechua, it.aimara) }
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    override fun getAllTemasLocal(): Flow<List<Tema>> {
        return localDataSource.getAllTemas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveTemasLocal(temas: List<Tema>) {
        localDataSource.insertTemas(temas.map { it.toEntity() })
    }

    private fun normalizarId(texto: String): String {
        val temp = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        val normalized = temp.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .replace(" ", "")
        return if (normalized == "cuerpohumano") "cuerpoHumano" else normalized
    }
}
