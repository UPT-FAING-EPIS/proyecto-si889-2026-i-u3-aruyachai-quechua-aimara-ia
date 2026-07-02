package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.TemasRepository
import javax.inject.Inject

class GetPalabrasPorTemaUseCase @Inject constructor(
    private val repository: TemasRepository
) {
    suspend operator fun invoke(nombreTema: String): Result<List<Palabra>> = 
        repository.getPalabrasPorTema(nombreTema)
}
