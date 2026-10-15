# VozPagos

App Android que escucha las notificaciones de la app de **Mercado Pago** y,
cuando te entra plata, dice el monto en voz alta por el altavoz.

**Límite de privacidad:** elegís un monto máximo. Si el pago lo supera,
la app dice solo "Recibiste un pago" (o nada, según elijas), así nadie
alrededor se entera de los montos grandes.

No necesita servidor, ni cuenta de desarrollador, ni tokens de Mercado Pago.

## Cómo instalarla

1. Instalá **Android Studio** (gratis) en tu computadora.
2. *File → Open* y elegí la carpeta `VozPagos`. Esperá que termine de sincronizar Gradle.
3. En el celular: *Ajustes → Acerca del teléfono* → tocá 7 veces "Número de compilación"
   para activar las opciones de desarrollador, y después activá **Depuración USB**.
4. Conectá el celular por USB y apretá ▶️ *Run* en Android Studio.

(Alternativa: *Build → Build APK(s)* y pasás el `.apk` al celular para instalarlo.)

## Primera configuración en el celular

1. Abrí **VozPagos** → **Dar permiso de notificaciones** → activá VozPagos.
2. Escribí el límite (ej. `50000`) y elegí qué hacer cuando lo supera → **Guardar**.
3. Probá con los botones "Simular pago".
4. Asegurate de que las notificaciones de Mercado Pago estén activadas.
5. **Importante:** en *Ajustes → Batería*, poné VozPagos y Mercado Pago como
   "Sin restricciones", si no Android puede cortar el servicio.

## Cómo funciona

| Archivo | Qué hace |
|---|---|
| `PagoListenerService.kt` | Recibe cada notificación del teléfono y filtra las de Mercado Pago |
| `LectorPagos.kt` | Decide si es un ingreso, saca el monto y aplica el límite |
| `Voz.kt` | Habla en español (Argentina) por el altavoz |
| `Ajustes.kt` | Guarda límite y preferencias |
| `MainActivity.kt` | Pantalla de configuración |

## Si no habla con algún pago

Mercado Pago puede cambiar el texto de sus notificaciones. Las palabras que
se buscan están al principio de `LectorPagos.kt` (`PALABRAS_INGRESO` y
`PALABRAS_EGRESO`): agregá la frase que te aparezca. Los tests en
`app/src/test` sirven para comprobarlo.

Si usás la app **Mercado Pago Point** (u otra), agregá su nombre de paquete a
`PAQUETES_MP`.
