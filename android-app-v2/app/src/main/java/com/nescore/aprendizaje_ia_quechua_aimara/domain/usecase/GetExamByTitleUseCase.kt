package com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Exam
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.PracticeRepository
import javax.inject.Inject

class GetExamByTitleUseCase @Inject constructor(
    private val repository: PracticeRepository
) {
    suspend operator fun invoke(language: String, level: String, title: String): Exam? {
        return repository.getExamByTitle(language, level, title)
    }
}
