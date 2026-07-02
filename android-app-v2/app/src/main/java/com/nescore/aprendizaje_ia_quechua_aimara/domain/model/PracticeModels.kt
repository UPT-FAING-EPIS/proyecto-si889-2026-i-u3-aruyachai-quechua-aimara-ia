package com.nescore.aprendizaje_ia_quechua_aimara.domain.model

import androidx.annotation.Keep

@Keep
data class Exam(
    val language: String,
    val level: String,
    val examTitle: String,
    val description: String = "",
    val questions: List<Question>,
    val achievement: Achievement
)

@Keep
data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

@Keep
data class Achievement(
    val name: String,
    val description: String,
    val shareMessage: String
)
