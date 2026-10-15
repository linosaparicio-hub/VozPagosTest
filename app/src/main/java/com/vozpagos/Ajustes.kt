package com.vozpagos

import android.content.Context

/** Configuración guardada en el teléfono. */
class Ajustes(context: Context) {
    private val prefs = context.getSharedPreferences("vozpagos", Context.MODE_PRIVATE)

    /** Activa o desactiva los avisos por voz. */
    var activo: Boolean
        get() = prefs.getBoolean("activo", true)
        set(v) = prefs.edit().putBoolean("activo", v).apply()

    /** Monto máximo (en pesos) que se dice en voz alta. 0 = sin límite. */
    var limite: Double
        get() = prefs.getFloat("limite", 50000f).toDouble()
        set(v) = prefs.edit().putFloat("limite", v.toFloat()).apply()

    /**
     * Qué hacer cuando el monto supera el límite:
     * true  = decir solo "Recibiste un pago" (sin monto)
     * false = no decir nada
     */
    var avisoDiscretoSobreLimite: Boolean
        get() = prefs.getBoolean("discreto", true)
        set(v) = prefs.edit().putBoolean("discreto", v).apply()
}
