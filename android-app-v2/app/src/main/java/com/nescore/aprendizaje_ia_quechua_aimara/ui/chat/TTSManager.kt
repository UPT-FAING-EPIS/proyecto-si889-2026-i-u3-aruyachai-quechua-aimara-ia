package com.nescore.aprendizaje_ia_quechua_aimara.ui.chat

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                // Lenguaje por defecto
                tts?.language = Locale("es", "PE")
            } else {
                Log.e("TTSManager", "Error al inicializar TTS")
            }
        }
    }

    fun speak(text: String, lang: String) {
        if (!isInitialized) return

        val locale = when (lang.lowercase()) {
            "quechua" -> Locale("qu", "PE")
            "aimara" -> Locale("ay", "PE")
            else -> Locale("es", "PE")
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Si no hay soporte específico, usamos español para que al menos se lea fonéticamente
            tts?.language = Locale("es", "PE")
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stop() {
        tts?.stop()
        tts?.shutdown()
    }
}
