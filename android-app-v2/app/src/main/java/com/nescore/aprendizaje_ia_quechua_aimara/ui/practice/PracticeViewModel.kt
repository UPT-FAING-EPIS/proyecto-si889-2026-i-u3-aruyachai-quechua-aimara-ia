package com.nescore.aprendizaje_ia_quechua_aimara.ui.practice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Exam
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.PracticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class PracticeListUiState(
    val language: String = "",
    val selectedLevel: String = "Fácil",
    val practices: List<Exam> = emptyList(),
    val completedPractices: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val repository: PracticeRepository,
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeListUiState())
    val uiState: StateFlow<PracticeListUiState> = _uiState.asStateFlow()

    fun init(language: String) {
        if (_uiState.value.language != language) {
            _uiState.update { it.copy(language = language) }
            loadPractices()
            loadCompletedPractices()
        }
    }

    fun setLevel(level: String) {
        if (_uiState.value.selectedLevel != level) {
            _uiState.update { it.copy(selectedLevel = level) }
            loadPractices()
        }
    }

    fun loadCompletedPractices() {
        val currentLanguage = _uiState.value.language.lowercase()
        if (currentLanguage.isEmpty()) return

        viewModelScope.launch {
            // 1. Cargar localmente primero
            val sharedPrefs = context.getSharedPreferences("practice_prefs_v2", Context.MODE_PRIVATE)
            val localSet = sharedPrefs.getStringSet("${currentLanguage}_completed_exams", emptySet()) ?: emptySet()
            _uiState.update { it.copy(completedPractices = localSet) }

            // 2. Cargar de Firebase en segundo plano
            val uid = auth.currentUser?.uid
            if (uid != null) {
                try {
                    val snapshot = database.getReference("progreso")
                        .child(uid)
                        .child("practicas")
                        .child(currentLanguage)
                        .get()
                        .await()
                    
                    if (snapshot.exists()) {
                        val firebaseSet = mutableSetOf<String>()
                        snapshot.children.forEach { levelSnap ->
                            levelSnap.children.forEach { examSnap ->
                                examSnap.key?.let { firebaseSet.add(it) }
                            }
                        }
                        if (firebaseSet.isNotEmpty()) {
                            val mergedSet = localSet.toMutableSet().apply { addAll(firebaseSet) }
                            sharedPrefs.edit().putStringSet("${currentLanguage}_completed_exams", mergedSet).apply()
                            _uiState.update { it.copy(completedPractices = mergedSet) }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadPractices() {
        val currentLanguage = _uiState.value.language
        val currentLevel = _uiState.value.selectedLevel
        
        if (currentLanguage.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val exams = repository.getExamsByLevel(currentLanguage, currentLevel)
            
            _uiState.update { 
                it.copy(
                    practices = exams,
                    isLoading = false
                )
            }
        }
    }
}
