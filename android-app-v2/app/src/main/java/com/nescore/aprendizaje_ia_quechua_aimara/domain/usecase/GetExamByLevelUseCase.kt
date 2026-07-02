package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Exam
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.PracticeRepository
import javax.inject.Inject

class GetExamByLevelUseCase @Inject constructor(
    private val repository: PracticeRepository
) {
    suspend operator fun invoke(language: String, level: String): List<Exam> {
        return repository.getExamsByLevel(language, level)
    }
}
