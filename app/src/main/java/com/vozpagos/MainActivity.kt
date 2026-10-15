package com.vozpagos

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var ajustes: Ajustes
    private var voz: Voz? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.firma)   // Título en la barra superior
        ajustes = Ajustes(this)

        val swActivo = findViewById<Switch>(R.id.swActivo)
        val etLimite = findViewById<EditText>(R.id.etLimite)
        val rbDiscreto = findViewById<RadioButton>(R.id.rbDiscreto)
        val rbSilencio = findViewById<RadioButton>(R.id.rbSilencio)

        swActivo.isChecked = ajustes.activo
        etLimite.setText(ajustes.limite.toLong().toString())
        rbDiscreto.isChecked = ajustes.avisoDiscretoSobreLimite
        rbSilencio.isChecked = !ajustes.avisoDiscretoSobreLimite

        swActivo.setOnCheckedChangeListener { _, on -> ajustes.activo = on }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val valor = etLimite.text.toString().replace(".", "").replace(",", ".").toDoubleOrNull()
            if (valor == null || valor < 0) {
                Toast.makeText(this, "Ingresá un monto válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ajustes.limite = valor
            ajustes.avisoDiscretoSobreLimite = rbDiscreto.isChecked
            Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnPermiso).setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        findViewById<Button>(R.id.btnProbarChico).setOnClickListener {
            probar("Recibiste \$ 1.500,50 de Juan Pérez")
        }
        findViewById<Button>(R.id.btnProbarGrande).setOnClickListener {
            probar("Te transfirieron \$ 250.000")
        }
    }

    override fun onResume() {
        super.onResume()
        val estado = findViewById<TextView>(R.id.tvEstado)
        estado.text = if (tienePermiso())
            "✅ Permiso de notificaciones activado"
        else
            "⚠️ Falta dar permiso para leer notificaciones"
    }

    override fun onDestroy() {
        voz?.cerrar()
        super.onDestroy()
    }

    /** Simula una notificación con la configuración actual. */
    private fun probar(texto: String) {
        val frase = LectorPagos.frase(texto, ajustes.limite, ajustes.avisoDiscretoSobreLimite)
        if (frase == null) {
            Toast.makeText(this, "Supera el límite: no se dice nada", Toast.LENGTH_SHORT).show()
            return
        }
        val v = voz ?: Voz(this).also { voz = it }
        v.decir(frase)
    }

    private fun tienePermiso(): Boolean {
        val habilitados = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: return false
        val yo = ComponentName(this, PagoListenerService::class.java).flattenToString()
        return habilitados.split(":").any { it == yo }
    }
}
