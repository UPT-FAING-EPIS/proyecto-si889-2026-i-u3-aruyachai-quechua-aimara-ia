package com.nescore.aprendizaje_ia_quechua_aimara.domain.repository

import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Exam

interface PracticeRepository {
    suspend fun getExamsByLevel(language: String, level: String): List<Exam>
    suspend fun getExamByTitle(language: String, level: String, title: String): Exam?
}
