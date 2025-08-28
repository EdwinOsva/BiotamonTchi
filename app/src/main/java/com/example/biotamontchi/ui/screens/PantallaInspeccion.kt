package com.example.biotamontchi.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.IntOffset
import com.example.biotamontchi.data.PrefsManager
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import com.example.biotamontchi.model.GameAudioViewModel2

// PantallaInspeccion.kt
data class PosRelativa(val x: Float, val y: Float) // valores entre 0f y 1f

@Composable
fun PantallaInspeccion(
    audioViewModel: GameAudioViewModel2,
    monedas: MutableState<Int>,
    prefs: PrefsManager,
    onSalir: () -> Unit
) {
    val fondos = listOf(
        R.drawable.fondoaerosol01,
        R.drawable.fondoaerosol02,
        R.drawable.fondoaerosol03
    )
    val fondoSeleccionado = remember { fondos.random() }

    val posicionesPorFondo = mapOf(
        R.drawable.fondoaerosol01 to listOf(
            PosRelativa(0.2f, 0.1f), PosRelativa(0.7f, 0.15f), PosRelativa(0.4f, 0.35f),
            PosRelativa(0.8f, 0.45f), PosRelativa(0.1f, 0.55f), PosRelativa(0.8f, 0.65f),
            PosRelativa(0.25f, 0.75f), PosRelativa(0.8f, 0.8f), PosRelativa(0.1f, 0.8f)
        ),
        R.drawable.fondoaerosol02 to listOf(
            PosRelativa(0.2f, 0.1f), PosRelativa(0.7f, 0.15f), PosRelativa(0.4f, 0.35f),
            PosRelativa(0.7f, 0.45f), PosRelativa(0.1f, 0.55f), PosRelativa(0.6f, 0.65f),
            PosRelativa(0.1f, 0.75f), PosRelativa(0.65f, 0.8f), PosRelativa(0.1f, 0.9f)
        ),
        R.drawable.fondoaerosol03 to listOf(
            PosRelativa(0.1f, 0.1f), PosRelativa(0.7f, 0.15f), PosRelativa(0.4f, 0.35f),
            PosRelativa(0.8f, 0.45f), PosRelativa(0.1f, 0.55f), PosRelativa(0.9f, 0.65f),
            PosRelativa(0.25f, 0.75f), PosRelativa(0.85f, 0.8f), PosRelativa(0.1f, 0.9f)
        )
    )

    val posiciones = posicionesPorFondo[fondoSeleccionado] ?: emptyList()

    val bichos = remember {
        mutableStateListOf<Bicho>().apply {
            addAll(generarBichosAleatorios(posiciones))
        }
    }

    var mostrarMensajeFin by remember { mutableStateOf(false) }

    // Música de fondo
    LaunchedEffect(Unit) {
        audioViewModel.startBackgroundMusic(R.raw.ambiente1)
    }

    DisposableEffect(Unit) {
        onDispose {
            audioViewModel.stopBackgroundMusic()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // opcional, si quieres ver un overlay
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent() // consume los toques para que no pasen atrás
                    }
                }
            }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = constraints.maxWidth.toFloat()
            val screenHeight = constraints.maxHeight.toFloat()

            // Fondo

            Image(
                painter = painterResource(id = fondoSeleccionado),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )


            // Bichos escalados
            bichos.forEach { bicho ->
                val posX = bicho.posicion.x * screenWidth
                val posY = bicho.posicion.y * screenHeight

                Box(
                    modifier = Modifier
                        .size((screenWidth * 0.1f).dp) // 20% del ancho
                        .offset { IntOffset(posX.toInt(), posY.toInt()) }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            audioViewModel.reproducirEfecto(R.raw.aplastar)
                            bichos.remove(bicho)
                        }
                ) {
                    Image(
                        painter = painterResource(id = bicho.recurso),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Verificamos si ya no hay bichos malos
            LaunchedEffect(bichos.count { it.tipo == TipoBicho.MALO }) {
                if (bichos.none { it.tipo == TipoBicho.MALO }) {
                    mostrarMensajeFin = true
                    delay(200)
                    // Cuando ya no quedan bichos malos:
                    monedas.value += 50
                    prefs.guardarMonedas(monedas.value)
                    prefs.sumarPuntos(10) // +10 puntos, por ejemplo

// Resetear plagas a 0
                    prefs.guardarIndicador("plagas", 0)

// Guardar fecha actual como fecha de inspección
                    val ahora = System.currentTimeMillis()
                    prefs.guardarLong("fechaUltimasPlagas", ahora)

                    onSalir()
                }
            }

            if (mostrarMensajeFin) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("¡Muy bien!", fontSize = 32.sp, color = Color.White)
                }
            }
        }
    }
}


data class Bicho(
    val posicion: PosRelativa,
    val recurso: Int,
    val tipo: TipoBicho
)

enum class TipoBicho { BUENO, MALO }
fun generarBichosAleatorios(posiciones: List<PosRelativa>): List<Bicho> {
    val bichosBuenos = listOf(
        R.drawable.bichosa1,
        R.drawable.bichosa5,
        R.drawable.bichosa6,
        R.drawable.bichosa7,
        R.drawable.bichosa8
    )

    val bichosMalos = listOf(
        R.drawable.bichosa2,
        R.drawable.bichosa3,
        R.drawable.bichosa4,
        R.drawable.bichosa9
    )

    val bichos = mutableListOf<Bicho>()
    val posicionesAleatorias = posiciones.shuffled().take(9)

    // Asegurar al menos un bicho malo
    val posicionMalo = posicionesAleatorias.random()
    bichos.add(
        Bicho(
            posicion = posicionMalo,
            recurso = bichosMalos.random(),
            tipo = TipoBicho.MALO
        )
    )

    // Agregar los otros 8 bichos al azar
    for (pos in posicionesAleatorias) {
        if (pos == posicionMalo) continue

        val esMalo = listOf(true, false).random()
        val recurso = if (esMalo) bichosMalos.random() else bichosBuenos.random()
        val tipo = if (esMalo) TipoBicho.MALO else TipoBicho.BUENO

        bichos.add(Bicho(pos, recurso, tipo))
    }

    return bichos
}
