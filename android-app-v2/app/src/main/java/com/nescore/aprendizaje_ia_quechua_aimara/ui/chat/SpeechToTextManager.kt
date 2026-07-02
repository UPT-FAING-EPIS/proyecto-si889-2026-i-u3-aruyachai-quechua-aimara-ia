package com.nescore.aprendizaje_ia_quechua_aimara.ui.chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class SpeechToTextManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    fun startListening(
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (String) -> Unit,
        onReady: () -> Unit
    ) {
        mainHandler.post {
            try {
                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    onError("El reconocimiento de voz no está disponible en este dispositivo")
                    return@post
                }

                // Reiniciar instancia para evitar Error 10 de Binder ocupado
                speechRecognizer?.destroy()
                
                // Forzar motor de Google si está disponible
                speechRecognizer = try {
                    val googleComponent = ComponentName(
                        "com.google.android.googlequicksearchbox",
                        "com.google.android.voicesearch.service.GoogleRecognitionService"
                    )
                    SpeechRecognizer.createSpeechRecognizer(context.applicationContext, googleComponent)
                } catch (e: Exception) {
                    SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
                }
                
                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d("ChatFlow", "STT: onReadyForSpeech")
                        mainHandler.post { onReady() }
                    }

                    override fun onBeginningOfSpeech() { Log.d("ChatFlow", "STT: Voz detectada") }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() { Log.d("ChatFlow", "STT: Fin de voz detectado") }

                    override fun onError(error: Int) {
                        mainHandler.post {
                            val message = when (error) {
                                SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                                SpeechRecognizer.ERROR_NO_MATCH -> "No se reconoció la voz"
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Servidor ocupado"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo agotado"
                                10 -> "Fallo de vinculación (Error 10)"
                                else -> "Error de voz ($error)"
                            }
                            Log.e("ChatFlow", "STT: onError -> $message ($error)")
                            onFinalResult("") // Notificar fin para liberar ViewModel
                            onError(message)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val finalResult = matches?.getOrNull(0) ?: ""
                        Log.d("ChatFlow", "STT: onResults -> '$finalResult'")
                        mainHandler.post { onFinalResult(finalResult) }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partialResult = matches?.getOrNull(0) ?: ""
                        if (partialResult.isNotEmpty()) {
                            Log.d("ChatFlow", "STT: onPartialResults -> '$partialResult'")
                            mainHandler.post { onPartialResult(partialResult) }
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
                
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es")
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                }
                
                Log.d("ChatFlow", "STT: startListening() llamado")
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e("ChatFlow", "STT: Excepción al iniciar -> ${e.message}")
                mainHandler.post { onError("Fallo al iniciar el grabador") }
            }
        }
    }

    fun stopListening() {
        mainHandler.post { 
            try {
                Log.d("ChatFlow", "STT: stopListening() llamado")
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e("ChatFlow", "STT: Error al detener -> ${e.message}")
            }
        }
    }

    fun destroy() {
        mainHandler.post {
            Log.d("ChatFlow", "STT: destroy()")
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
}
