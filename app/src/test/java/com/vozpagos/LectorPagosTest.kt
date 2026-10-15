package com.vozpagos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LectorPagosTest {

    @Test fun montoConMilesYCentavos() =
        assertEquals(1500.5, LectorPagos.extraerMonto("Recibiste \$ 1.500,50")!!, 0.001)

    @Test fun montoSimple() =
        assertEquals(800.0, LectorPagos.extraerMonto("Te transfirieron \$800")!!, 0.001)

    @Test fun debajoDelLimiteDiceMonto() =
        assertEquals("Recibiste 1500 pesos con 50 centavos",
            LectorPagos.frase("Recibiste \$ 1.500,50 de Juan", 50000.0, true))

    @Test fun sobreLimiteModoDiscreto() =
        assertEquals("Recibiste un pago",
            LectorPagos.frase("Te transfirieron \$ 250.000", 50000.0, true))

    @Test fun sobreLimiteModoSilencio() =
        assertNull(LectorPagos.frase("Te transfirieron \$ 250.000", 50000.0, false))

    @Test fun sinLimite() =
        assertEquals("Recibiste 250000 pesos",
            LectorPagos.frase("Te transfirieron \$ 250.000", 0.0, true))

    @Test fun egresoSeIgnora() =
        assertNull(LectorPagos.frase("Pagaste \$ 2.000 en Kiosco", 0.0, true))
}
