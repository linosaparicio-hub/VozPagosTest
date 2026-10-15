package com.vozpagos

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Android llama a este servicio cada vez que llega una notificación.
 * Solo mira las de Mercado Pago y, si es un ingreso, lo dice en voz alta
 * respetando el límite configurado.
 */
class PagoListenerService : NotificationListenerService() {

    private lateinit var voz: Voz
    private lateinit var ajustes: Ajustes

    // Evita repetir la misma notificación (Android a veces la manda 2 veces)
    private val recientes = LinkedHashMap<String, Long>()

    override fun onCreate() {
        super.onCreate()
        voz = Voz(this)
        ajustes = Ajustes(this)
    }

    override fun onDestroy() {
        voz.cerrar()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in LectorPagos.PAQUETES_MP) return
        if (!ajustes.activo) return

        val extras = sbn.notification.extras
        val titulo = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val texto = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val largo = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val completo = "$titulo. $texto. $largo"

        if (esRepetida(completo)) return

        val frase = LectorPagos.frase(
            completo, ajustes.limite, ajustes.avisoDiscretoSobreLimite
        ) ?: return
        voz.decir(frase)
    }

    private fun esRepetida(clave: String): Boolean {
        val ahora = System.currentTimeMillis()
        recientes.entries.removeAll { ahora - it.value > 15_000 }
        if (recientes.containsKey(clave)) return true
        recientes[clave] = ahora
        return false
    }
}
