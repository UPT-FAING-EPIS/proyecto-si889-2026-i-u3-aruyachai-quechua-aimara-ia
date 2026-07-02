package com.nescore.aprendizaje_ia_quechua_aimara.ui.practice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Exam
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetExamByLevelUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetExamByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExamUiState(
    val exam: Exam? = null,
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val selectedOption: String? = null,
    val isAnswerChecked: Boolean = false,
    val isExamCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val getExamByLevelUseCase: GetExamByLevelUseCase,
    private val getExamByTitleUseCase: GetExamByTitleUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    fun loadExam(language: String, level: String, examTitle: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val exam = getExamByTitleUseCase(language, level, examTitle)
            if (exam != null) {
                _uiState.update { it.copy(exam = exam, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo cargar el examen") }
            }
        }
    }

    fun onOptionSelected(option: String) {
        if (_uiState.value.isAnswerChecked) return
        _uiState.update { it.copy(selectedOption = option) }
    }

    fun checkAnswer() {
        val state = _uiState.value
        val currentQuestion = state.exam?.questions?.getOrNull(state.currentQuestionIndex) ?: return
        val isCorrect = state.selectedOption == currentQuestion.correctAnswer

        _uiState.update {
            it.copy(
                isAnswerChecked = true,
                score = if (isCorrect) it.score + 1 else it.score
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1
        val totalQuestions = state.exam?.questions?.size ?: 0

        if (nextIndex < totalQuestions) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOption = null,
                    isAnswerChecked = false
                )
            }
        } else {
            _uiState.update { it.copy(isExamCompleted = true) }
            saveExamProgress()
        }
    }

    private fun saveExamProgress() {
        val state = _uiState.value
        val exam = state.exam ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        viewModelScope.launch {
            try {
                val lang = exam.language.lowercase()
                val level = exam.level.lowercase()
                val safeTitle = exam.examTitle
                    .replace(".", "_")
                    .replace("#", "_")
                    .replace("$", "_")
                    .replace("[", "_")
                    .replace("]", "_")
                    .replace("/", "_")
                
                // 1. Guardar localmente de forma inmediata (offline compatible)
                val sharedPrefs = context.getSharedPreferences("practice_prefs_v2", Context.MODE_PRIVATE)
                val completedSet = sharedPrefs.getStringSet("${lang}_completed_exams", emptySet())?.toMutableSet() ?: mutableSetOf()
                completedSet.add(safeTitle)
                sharedPrefs.edit().putStringSet("${lang}_completed_exams", completedSet).apply()

                // 2. Guardar en Firebase (asíncrono)
                val database = FirebaseDatabase.getInstance()
                database.getReference("progreso")
                    .child(uid)
                    .child("practicas")
                    .child(lang)
                    .child(level)
                    .child(safeTitle)
                    .setValue(true)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            com.nescore.aprendizaje_ia_quechua_aimara.util.LeaderboardHelper.updateLeaderboard(uid)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetExam() {
        _uiState.update {
            it.copy(
                currentQuestionIndex = 0,
                score = 0,
                selectedOption = null,
                isAnswerChecked = false,
                isExamCompleted = false
            )
        }
    }
}
