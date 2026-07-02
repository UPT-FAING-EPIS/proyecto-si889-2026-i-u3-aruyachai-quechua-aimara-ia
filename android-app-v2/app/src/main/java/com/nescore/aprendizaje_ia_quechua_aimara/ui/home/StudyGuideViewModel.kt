package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.AssessPronunciationUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetPalabrasPorTemaUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetTemasUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.ui.chat.TTSManager
import com.nescore.aprendizaje_ia_quechua_aimara.util.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StudyGuideViewModel @Inject constructor(
    application: Application,
    private val getTemasUseCase: GetTemasUseCase,
    private val getPalabrasPorTemaUseCase: GetPalabrasPorTemaUseCase,
    private val assessPronunciationUseCase: AssessPronunciationUseCase,
    private val ttsManager: TTSManager,
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("study_guide_prefs_v2", Context.MODE_PRIVATE)
    private val audioRecorder = AudioRecorder(application)
    private var currentAudioFile: File? = null
    private val storage = FirebaseStorage.getInstance()
    private var recordStartTime = 0L

    // Devuelve el UID del usuario actual (logueado o anónimo)
    // Firebase Auth siempre proporciona un UID incluso para sesiones anónimas
    private fun getCurrentUid(): String? = auth.currentUser?.uid

    private val _uiState = MutableStateFlow(StudyGuideUiState())
    val uiState: StateFlow<StudyGuideUiState> = _uiState.asStateFlow()

    init {
        loadTemas()
    }

    fun loadTemas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getTemasUseCase().onSuccess { temas ->
                val progressMap = mutableMapOf<String, Int>()
                val uid = getCurrentUid()
                val lang = _uiState.value.language.lowercase()

                // Cargar primero el progreso local desde SharedPreferences
                temas.forEach { tema ->
                    val completedSetLocal = sharedPrefs.getStringSet("${lang}_${tema.id}", emptySet()) ?: emptySet()
                    progressMap[tema.id] = completedSetLocal.size
                }

                // Cargar e integrar progreso de Firebase en segundo plano
                if (uid != null) {
                    try {
                        val snapshot = database
                            .getReference("progreso")
                            .child(uid)
                            .child("guia")
                            .child(lang)
                            .get()
                            .await()
                        temas.forEach { tema ->
                            val temaSnap = snapshot.child(tema.id)
                            val completedWordsFirebase = temaSnap.children.mapNotNull { it.key }.toSet()
                            if (completedWordsFirebase.isNotEmpty()) {
                                val localSet = sharedPrefs.getStringSet("${lang}_${tema.id}", emptySet())?.toMutableSet() ?: mutableSetOf()
                                localSet.addAll(completedWordsFirebase)
                                sharedPrefs.edit().putStringSet("${lang}_${tema.id}", localSet).apply()
                                progressMap[tema.id] = localSet.size
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("StudyGuideVM", "Error cargando progreso de Firebase para $lang", e)
                    }
                }
                _uiState.update { it.copy(temas = temas, progress = progressMap, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectTema(tema: Tema) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    activeTema = tema, 
                    currentWordIndex = 0, 
                    sttResult = null, 
                    feedback = null, 
                    isCorrect = null,
                    completedWords = emptySet()
                ) 
            }
            
            val lang = _uiState.value.language.lowercase()
            
            // Cargar primero localmente
            val completedSet = sharedPrefs.getStringSet("${lang}_${tema.id}", emptySet())?.toMutableSet() ?: mutableSetOf()
            
            // Cargar desde Firebase de fondo
            val uid = getCurrentUid()
            if (uid != null) {
                try {
                    val snapshot = database
                        .getReference("progreso")
                        .child(uid)
                        .child("guia")
                        .child(lang)
                        .child(tema.id)
                        .get()
                        .await()
                    snapshot.children.forEach { childSnap ->
                        childSnap.key?.let { completedSet.add(it) }
                    }
                    // Guardar localmente
                    sharedPrefs.edit().putStringSet("${lang}_${tema.id}", completedSet).apply()
                } catch (e: Exception) {
                    Log.e("StudyGuideVM", "Error cargando palabras completadas para $lang", e)
                }
            }

            getPalabrasPorTemaUseCase(tema.nombre).onSuccess { palabras ->
                // Buscar el índice de la primera palabra que aún no ha sido completada
                var firstPendingIndex = 0
                for (i in palabras.indices) {
                    val w = palabras[i]
                    val safeKey = w.espanol
                        .replace(".", "_")
                        .replace("#", "_")
                        .replace("$", "_")
                        .replace("[", "_")
                        .replace("]", "_")
                        .replace("/", "_")
                    if (!completedSet.contains(safeKey)) {
                        firstPendingIndex = i
                        break
                    }
                }

                _uiState.update { 
                    it.copy(
                        palabras = palabras, 
                        completedWords = completedSet,
                        currentWordIndex = firstPendingIndex,
                        isLoading = false 
                    ) 
                }
                if (palabras.isNotEmpty()) {
                    speakCurrentWord()
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deselectTema() {
        stopRecording()
        _uiState.update { 
            it.copy(
                activeTema = null, 
                palabras = emptyList(), 
                currentWordIndex = 0, 
                sttResult = null, 
                feedback = null, 
                isCorrect = null
            ) 
        }
        loadTemas()
    }

    fun speakCurrentWord() {
        val state = _uiState.value
        val word = state.palabras.getOrNull(state.currentWordIndex) ?: return
        val targetText = when (state.language.lowercase()) {
            "quechua" -> word.quechua
            "aimara" -> word.aimara
            else -> word.espanol
        }
        ttsManager.speak(targetText, state.language)
    }

    fun setLanguage(language: String) {
        val oldLang = _uiState.value.language
        if (oldLang.lowercase() != language.lowercase()) {
            _uiState.update { it.copy(language = language) }
            loadTemas()
        }
    }

    fun startRecording() {
        try {
            val file = File(getApplication<Application>().cacheDir, "audio_assess_${UUID.randomUUID()}.m4a")
            currentAudioFile = file
            recordStartTime = System.currentTimeMillis()
            audioRecorder.startRecording(file)
            _uiState.update { it.copy(isListening = true, sttResult = null, feedback = null, isCorrect = null) }
        } catch (e: Exception) {
            Log.e("StudyGuideViewModel", "Error starting recording", e)
            _uiState.update { it.copy(isListening = false, feedback = "Error al iniciar el micrófono", isCorrect = false) }
        }
    }

    fun stopRecording() {
        if (_uiState.value.isListening) {
            _uiState.update { it.copy(isListening = false) }
            try {
                audioRecorder.stopRecording()
                val duration = System.currentTimeMillis() - recordStartTime
                if (duration < 800) {
                    _uiState.update { it.copy(feedback = "El audio es demasiado corto. Intenta hablar un poco más.", isCorrect = false) }
                    currentAudioFile?.let {
                        if (it.exists()) it.delete()
                    }
                    return
                }
                uploadAndAssessAudio()
            } catch (e: Exception) {
                Log.e("StudyGuideViewModel", "Error stopping recording", e)
            }
        }
    }

    private fun uploadAndAssessAudio() {
        val file = currentAudioFile ?: return
        if (!file.exists() || file.length() == 0L) return

        _uiState.update { it.copy(isAnalyzing = true, feedback = "Analizando pronunciación con IA...", isCorrect = null) }

        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: run {
                    _uiState.update { it.copy(isAnalyzing = false, feedback = "Sesión de usuario no detectada.", isCorrect = false) }
                    return@launch
                }

                val fileName = "audios/pronunciation/${user.uid}/${UUID.randomUUID()}.m4a"
                val audioRef = storage.reference.child(fileName)
                audioRef.putFile(Uri.fromFile(file)).await()

                user.getIdToken(true).await()

                val state = _uiState.value
                val word = state.palabras.getOrNull(state.currentWordIndex) ?: return@launch
                val targetWord = when (state.language.lowercase()) {
                    "quechua" -> word.quechua
                    "aimara" -> word.aimara
                    else -> word.espanol
                }

                val cleanPath = audioRef.path.removePrefix("/")
                assessPronunciationUseCase(cleanPath, targetWord, state.language, word.espanol)
                    .onSuccess { data ->
                        val transcription = data["transcription"] as? String ?: ""
                        val isCorrect = data["isCorrect"] as? Boolean ?: false
                        val feedback = data["feedback"] as? String ?: ""

                        _uiState.update { 
                            it.copy(
                                isAnalyzing = false, 
                                sttResult = transcription,
                                isCorrect = isCorrect,
                                feedback = feedback
                            ) 
                        }

                        if (isCorrect) {
                            saveWordCompleted(state.activeTema?.id ?: "", word.espanol)
                        }
                    }
                    .onFailure { error ->
                        Log.e("StudyGuideViewModel", "Error in pronunciation assessment", error)
                        _uiState.update { 
                            it.copy(
                                isAnalyzing = false, 
                                feedback = "Error al evaluar con IA: ${error.message}", 
                                isCorrect = false
                            ) 
                        }
                    }
            } catch (e: Exception) {
                Log.e("StudyGuideViewModel", "Exception in pronunciation assessment flow", e)
                _uiState.update { 
                    it.copy(
                        isAnalyzing = false, 
                        feedback = "Error de red o conexión: ${e.localizedMessage}", 
                        isCorrect = false
                    ) 
                }
            } finally {
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    fun previousWord() {
        val state = _uiState.value
        if (state.currentWordIndex > 0) {
            _uiState.update {
                it.copy(
                    currentWordIndex = it.currentWordIndex - 1,
                    sttResult = null,
                    feedback = null,
                    isCorrect = null
                )
            }
            speakCurrentWord()
        }
    }

    fun nextWord() {
        val state = _uiState.value
        if (state.currentWordIndex < state.palabras.size - 1) {
            _uiState.update { 
                it.copy(
                    currentWordIndex = it.currentWordIndex + 1, 
                    sttResult = null, 
                    feedback = null, 
                    isCorrect = null
                ) 
            }
            speakCurrentWord()
        } else {
            _uiState.update { it.copy(feedback = "¡Felicidades! Has completado la lección.", isCorrect = true) }
        }
    }

    fun resetEvaluation() {
        _uiState.update { 
            it.copy(
                sttResult = null,
                feedback = null,
                isCorrect = null
            )
        }
    }

    private fun saveWordCompleted(temaId: String, wordEspanol: String) {
        // Sanitizamos la clave para que sea válida en Firebase RTDB (sin puntos, corchetes, etc.)
        val safeKey = wordEspanol
            .replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
            .replace("/", "_")
        
        val lang = _uiState.value.language.lowercase()

        // 1. Guardar localmente de forma inmediata (siempre funciona, offline compatible)
        val completedSetLocal = sharedPrefs.getStringSet("${lang}_${temaId}", emptySet())?.toMutableSet() ?: mutableSetOf()
        completedSetLocal.add(safeKey)
        sharedPrefs.edit().putStringSet("${lang}_${temaId}", completedSetLocal).apply()

        val updatedProgress = _uiState.value.progress.toMutableMap()
        updatedProgress[temaId] = completedSetLocal.size
        _uiState.update { 
            it.copy(
                progress = updatedProgress,
                completedWords = completedSetLocal
            ) 
        }

        // 2. Guardar en Firebase (asíncrono)
        val uid = getCurrentUid()
        if (uid != null) {
            val ref = database
                .getReference("progreso")
                .child(uid)
                .child("guia")
                .child(lang)
                .child(temaId)
                .child(safeKey)

            ref.setValue(true)
                .addOnSuccessListener {
                    Log.d("StudyGuideVM", "Progreso guardado en Firebase: $temaId / $safeKey")
                    viewModelScope.launch {
                        com.nescore.aprendizaje_ia_quechua_aimara.util.LeaderboardHelper.updateLeaderboard(uid)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("StudyGuideVM", "Error guardando progreso en Firebase", e)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.stopRecording()
    }
}

data class StudyGuideUiState(
    val temas: List<Tema> = emptyList(),
    val progress: Map<String, Int> = emptyMap(),
    val activeTema: Tema? = null,
    val palabras: List<Palabra> = emptyList(),
    val completedWords: Set<String> = emptySet(),
    val currentWordIndex: Int = 0,
    val language: String = "quechua",
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isListening: Boolean = false,
    val sttResult: String? = null,
    val feedback: String? = null,
    val isCorrect: Boolean? = null,
    val error: String? = null
)
