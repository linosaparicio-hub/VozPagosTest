package com.vozpagos

/**
 * Lógica pura (sin Android) para decidir si una notificación es un ingreso
 * de dinero, extraer el monto y armar la frase a decir.
 */
object LectorPagos {

    /** Paquetes de la app de Mercado Pago (Android). */
    val PAQUETES_MP = setOf("com.mercadopago.wallet")

    // Frases que indican que ENTRÓ plata
    private val PALABRAS_INGRESO = listOf(
        "recibiste", "te transfiri", "te envi", "te pag", "cobraste",
        "ingres", "te depositaron", "acreditamos", "se acredit"
    )

    // Frases que indican que SALIÓ plata (se ignoran)
    private val PALABRAS_EGRESO = listOf(
        "pagaste", "transferiste", "enviaste", "debitamos", "se debit",
        "compraste", "retiraste", "vence", "pagá", "pagar"
    )

    /** Busca "$ 1.234,56", "$1234" o "1.234,56 pesos". */
    private val REGEX_MONTO = Regex(
        """\$\s?([0-9]{1,3}(?:\.[0-9]{3})+(?:,[0-9]{1,2})?|[0-9]+(?:,[0-9]{1,2})?)"""
    )

    fun esIngreso(texto: String): Boolean {
        val t = texto.lowercase()
        if (PALABRAS_EGRESO.any { t.contains(it) }) return false
        return PALABRAS_INGRESO.any { t.contains(it) }
    }

    /** Devuelve el primer monto encontrado en pesos, o null. Formato argentino. */
    fun extraerMonto(texto: String): Double? {
        val m = REGEX_MONTO.find(texto) ?: return null
        val limpio = m.groupValues[1].replace(".", "").replace(",", ".")
        return limpio.toDoubleOrNull()
    }

    /** Convierte 1500.5 en "1500 pesos con 50 centavos". */
    fun montoHablado(monto: Double): String {
        val pesos = monto.toLong()
        val centavos = Math.round((monto - pesos) * 100)
        val base = if (pesos == 1L) "1 peso" else "$pesos pesos"
        return if (centavos > 0) "$base con $centavos centavos" else base
    }

    /**
     * Decide qué decir. Devuelve null si no hay que decir nada.
     * @param limite 0 = sin límite.
     */
    fun frase(texto: String, limite: Double, discretoSobreLimite: Boolean): String? {
        if (!esIngreso(texto)) return null
        val monto = extraerMonto(texto)
            ?: return if (discretoSobreLimite) "Recibiste un pago" else null
        val superaLimite = limite > 0 && monto > limite
        return when {
            !superaLimite -> "Recibiste ${montoHablado(monto)}"
            discretoSobreLimite -> "Recibiste un pago"
            else -> null
        }
    }
}
