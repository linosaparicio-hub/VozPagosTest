package com.vozpagos

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Envoltorio simple del motor de voz del teléfono, en español de Argentina. */
class Voz(context: Context) : TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context.applicationContext, this)
    private var listo = false
    private val pendientes = mutableListOf<String>()

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val r = tts.setLanguage(Locale("es", "AR"))
        if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(Locale("es"))
        }
        // Sale por el altavoz como una notificación
        tts.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        listo = true
        pendientes.forEach { decir(it) }
        pendientes.clear()
    }

    fun decir(texto: String) {
        if (!listo) { pendientes.add(texto); return }
        tts.speak(texto, TextToSpeech.QUEUE_ADD, null, texto.hashCode().toString())
    }

    fun cerrar() {
        tts.stop()
        tts.shutdown()
    }
}
