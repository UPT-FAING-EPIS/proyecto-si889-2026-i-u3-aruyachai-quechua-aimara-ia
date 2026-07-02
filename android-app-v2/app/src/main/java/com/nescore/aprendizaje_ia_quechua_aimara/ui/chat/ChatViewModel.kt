package com.nescore.aprendizaje_ia_quechua_aimara.ui.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.ChatMessage
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.MessageRole
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetAIResponseUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.ChatRepository
import com.nescore.aprendizaje_ia_quechua_aimara.util.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAIResponseUseCase: GetAIResponseUseCase,
    private val chatRepository: ChatRepository,
    private val ttsManager: TTSManager
) : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    var inputText by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isRecording by mutableStateOf(false)
        private set
    var sttError by mutableStateOf<String?>(null)
        private set

    private val storage = FirebaseStorage.getInstance()
    private val audioRecorder = AudioRecorder(context)
    private var currentAudioFile: File? = null
    private var recordStartTime = 0L

    fun onInputChange(newValue: String) { inputText = newValue }
    fun speak(text: String, lang: String) { ttsManager.speak(text, lang) }

    fun startRecording() {
        Log.d("ChatFlow", "[STEP 1] Iniciando grabación física")
        try {
            val file = File(context.cacheDir, "audio_${UUID.randomUUID()}.m4a")
            currentAudioFile = file
            recordStartTime = System.currentTimeMillis()
            audioRecorder.startRecording(file)
            isRecording = true
            sttError = null
        } catch (e: Exception) {
            Log.e("ChatFlow", "[ERROR] Fallo al iniciar grabación", e)
            sttError = "Error de micrófono"
            isRecording = false
        }
    }

    fun stopRecording() {
        if (isRecording) {
            Log.d("ChatFlow", ">>> DETENCIÓN MANUAL")
            isRecording = false
            try {
                audioRecorder.stopRecording()
                val duration = System.currentTimeMillis() - recordStartTime
                if (duration < 800) {
                    Log.d("ChatFlow", "Audio demasiado corto: ${duration}ms")
                    sttError = "El audio es demasiado corto"
                    currentAudioFile?.let {
                        if (it.exists()) it.delete()
                    }
                    return
                }
                uploadAndProcessAudio()
            } catch (e: Exception) {
                Log.e("ChatFlow", "Fallo al detener micro", e)
            }
        }
    }

    private fun uploadAndProcessAudio() {
        val file = currentAudioFile ?: return
        if (!file.exists() || file.length() == 0L) return

        isLoading = true
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: run {
                    Log.e("ChatFlow", "No hay usuario activo")
                    _messages.add(ChatMessage(content = "Error: Sesión no encontrada.", role = MessageRole.ASSISTANT))
                    return@launch
                }

                // 1. Subida a Storage
                val fileName = "audios/${user.uid}/${UUID.randomUUID()}.m4a"
                val audioRef = storage.reference.child(fileName)
                
                Log.d("ChatFlow", "Subiendo audio a: ${audioRef.path}")
                audioRef.putFile(Uri.fromFile(file)).await()

                // 2. REFRESCAR TOKEN (Solución definitiva al error UNAUTHENTICATED)
                // Se hace después de la subida por si esta tomó mucho tiempo.
                Log.d("ChatFlow", "Forzando refresco de token antes de llamar a la Function...")
                user.getIdToken(true).await()

                // 3. Llamar a la Function enviando el PATH limpio
                val cleanPath = audioRef.path.removePrefix("/")
                Log.d("ChatFlow", "Llamando a Backend con Path: $cleanPath")

                chatRepository.getAIAudioResponse(cleanPath)
                    .onSuccess { data ->
                        val transcription = data["transcription"] ?: ""
                        val response = data["response"] ?: ""
                        val feedback = data["feedback"] ?: ""

                        _messages.add(ChatMessage(content = "(Voz): $transcription", role = MessageRole.USER))
                        _messages.add(ChatMessage(content = "$response\n\n\n$feedback", role = MessageRole.ASSISTANT))
                    }
                    .onFailure { error ->
                        Log.e("ChatFlow", "Error en Function", error)
                        _messages.add(ChatMessage(content = "IA Error: ${error.message}", role = MessageRole.ASSISTANT))
                    }

            } catch (e: Exception) {
                Log.e("ChatFlow", "Excepción en flujo", e)
                _messages.add(ChatMessage(content = "Error de conexión o autenticación: ${e.localizedMessage}", role = MessageRole.ASSISTANT))
            } finally {
                isLoading = false
                if (file.exists()) file.delete()
            }
        }
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isEmpty() || isLoading) return
        val user = FirebaseAuth.getInstance().currentUser ?: return
        _messages.add(ChatMessage(content = text, role = MessageRole.USER))
        inputText = ""
        isLoading = true
        viewModelScope.launch {
            try {
                user.getIdToken(true).await()
                getAIResponseUseCase(text).onSuccess { response ->
                    _messages.add(ChatMessage(content = response, role = MessageRole.ASSISTANT))
                }.onFailure { error ->
                    _messages.add(ChatMessage(content = "Error del servidor: ${error.message}", role = MessageRole.ASSISTANT))
                }
            } catch (e: Exception) {
                _messages.add(ChatMessage(content = "Error de conexión.", role = MessageRole.ASSISTANT))
            } finally {
                isLoading = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
