package com.example.biotamontchi.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.example.biotamontchi.data.PrefsManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.example.biotamontchi.model.GameAudioViewModel2
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun PantallaMemorama(
    audioViewModel: GameAudioViewModel2,
    onSalir: () -> Unit,
    prefs: PrefsManager,
    monedas: MutableState<Int>
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // Fondo semitransparente opcional
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent() // Esto consume los toques
                    }
                }
            }
    ) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Botón regresar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.iconregresar),
                contentDescription = "Regresar",
                modifier = Modifier
                    .size(50.dp)
                    .clickable { onSalir() }
            )
        }


      //  TableroConCartasYVolteo
        TableroConCartasYLogica(
            audioViewModel = audioViewModel,
            onSalir = {
                // Aquí lo que quieras hacer cuando termine el juego
               onSalir()
            },
            prefs =  prefs,
             monedas =  monedas

        )

    }
}



@Composable
fun CartaMemoramaLogica(
    index: Int,
    dorsoId: Int,
    frenteId: Int,
    volteada: Boolean,
    onVoltear: () -> Unit
) {
    val rotacion by animateFloatAsState(
        targetValue = if (volteada) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = ""
    )

    Box(
        modifier = Modifier
            .size(50.dp)
            .graphicsLayer {
                rotationY = rotacion
                cameraDistance = 8 * density
            }
            .clickable(enabled = !volteada) { onVoltear() },
        contentAlignment = Alignment.Center
    ) {
        if (rotacion <= 90f) {
            Image(
                painter = painterResource(id = dorsoId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = frenteId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            )
        }
    }
}


@Composable
fun TableroConCartasYLogica(audioViewModel: GameAudioViewModel2, onSalir: () -> Unit,  prefs: PrefsManager, monedas: MutableState<Int>) {
    val tableroBase = listOf(
        listOf(4,4,4,4,4,4),
        listOf(4,3,3,3,3,4),
        listOf(4,3,2,2,3,4),
        listOf(4,3,3,3,3,4),
        listOf(4,4,4,4,4,4)
    )

    val fonditos = mapOf(
        1 to R.drawable.fonditojuego1,
        2 to R.drawable.fonditojuego2,
        3 to R.drawable.fonditojuego3,
        4 to R.drawable.fonditojuego4
    )

    val context = LocalContext.current

    // Generar cartas solo una vez
    val listaCompleta = remember {
        val frutasDisponibles = (2..14).map { "tarjeta$it" }
        val frutasSeleccionadas = frutasDisponibles.shuffled().take(7)
        val cartasUsadas = (frutasSeleccionadas + frutasSeleccionadas)
        val cartasConIds = cartasUsadas.map { nombre ->
            context.resources.getIdentifier(nombre, "drawable", context.packageName)
        }
        (cartasConIds + List(7) { null }).shuffled()
    }

    // Estado del juego
    var primeraSeleccion by remember { mutableStateOf<Int?>(null) }
    var segundaSeleccion by remember { mutableStateOf<Int?>(null) }
    var cartasVolteadas by remember { mutableStateOf(MutableList(listaCompleta.size) { false }) }
    var errores by remember { mutableStateOf(0) }
    var aciertos by remember { mutableStateOf(0) }

    // Estado pre-memoria
    var preMemoria by remember { mutableStateOf(true) }
// Estado para el mensaje de fin de juego
    val mensajeFinal = remember { mutableStateOf<String?>(null) }
    var monedasGanadas by remember { mutableStateOf(0) }
    // Pre-memoria: girar todas las cartas 2 veces
    LaunchedEffect(Unit) {
        if (preMemoria) {
            cartasVolteadas = MutableList(listaCompleta.size) { true }
            delay(1000)
            cartasVolteadas = MutableList(listaCompleta.size) { false }
            delay(500)
            cartasVolteadas = MutableList(listaCompleta.size) { true }
            delay(1000)
            cartasVolteadas = MutableList(listaCompleta.size) { false }
            preMemoria = false
        }
    }

    // Comprobar pares
    LaunchedEffect(segundaSeleccion) {
        if (primeraSeleccion != null && segundaSeleccion != null) {
            val idx1 = primeraSeleccion!!
            val idx2 = segundaSeleccion!!

            if (listaCompleta[idx1] == listaCompleta[idx2]) {
                // ✅ Acierto
                val puntosFicha1 = obtenerPuntosPorFicha(idx1, tableroBase)
                val puntosFicha2 = obtenerPuntosPorFicha(idx2, tableroBase)
                monedasGanadas+= (puntosFicha1 + puntosFicha2)
                monedas.value += (puntosFicha1 + puntosFicha2)
                prefs.guardarMonedas(monedas.value)
                audioViewModel.reproducirEfecto(R.raw.ganar_par)
                aciertos++
                prefs.sumarPuntos(1) // +10 puntos, por ejemplo
                primeraSeleccion = null
                segundaSeleccion = null
            } else {
                // ❌ Error
                audioViewModel.reproducirEfecto(R.raw.clic5)
                errores++
                delay(1000)
                cartasVolteadas[idx1] = false
                cartasVolteadas[idx2] = false
                primeraSeleccion = null
                segundaSeleccion = null
            }

            if (errores >= 3) {
                // Bloqueamos y salimos
                mensajeFinal.value = "Juego no completado 😢 ¡Ganaste ${monedasGanadas} monedas! (Presiona Regresar)"
                audioViewModel.reproducirEfecto(R.raw.perderfinal)
                val ahora = System.currentTimeMillis()
                val nuevaFelicidad = (prefs.obtenerInt("feliz") + 5).coerceAtMost(10)
                prefs.guardarIndicador("feliz", nuevaFelicidad)
                prefs.guardarLong("fechaUltimaFelicidad", ahora)
                delay(2000) // opcional, para que se vea la última carta
                onSalir()

            }


            if (aciertos >= 7) {
                mensajeFinal.value = "Fin del juego 🎉 ¡Ganaste ${monedasGanadas} monedas! (Presiona Regresar)"
                audioViewModel.reproducirEfecto(R.raw.ganarfinal)
                val ahora = System.currentTimeMillis()
                val nuevaFelicidad = (prefs.obtenerInt("feliz") + 5).coerceAtMost(10)
                prefs.guardarIndicador("feliz", nuevaFelicidad)
                prefs.guardarLong("fechaUltimaFelicidad", ahora)
                delay(2000)
                onSalir()

            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Mostrar errores
            val erroresImgId = context.resources.getIdentifier("errores$errores", "drawable", context.packageName)
            Image(
                painter = painterResource(id = erroresImgId),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tablero
            Column {
                for (filaIndex in tableroBase.indices) {
                    Row {
                        for (colIndex in tableroBase[filaIndex].indices) {
                            val fondoId = fonditos[tableroBase[filaIndex][colIndex]] ?: R.drawable.fonditojuego1
                            val cartaIndex = filaIndex * 6 + colIndex
                            val cartaId = listaCompleta.getOrNull(cartaIndex)

                            Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = fondoId),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                cartaId?.let { frenteId ->
                                    CartaMemoramaLogica(
                                        index = cartaIndex,
                                        dorsoId = R.drawable.tarjeta1,
                                        frenteId = frenteId,
                                        volteada = cartasVolteadas[cartaIndex],
                                        onVoltear = {
                                            if (!preMemoria && errores < 3) { // ❌ Bloquea si ya hay 3 errores
                                                if (primeraSeleccion == null) {
                                                    primeraSeleccion = cartaIndex
                                                    cartasVolteadas[cartaIndex] = true
                                                    audioViewModel.reproducirEfecto(R.raw.clic2)
                                                } else if (segundaSeleccion == null && cartaIndex != primeraSeleccion) {
                                                    segundaSeleccion = cartaIndex
                                                    cartasVolteadas[cartaIndex] = true
                                                    audioViewModel.reproducirEfecto(R.raw.clic2)
                                                }
                                            }
                                        }
                                    )
                                }

                            }
                        }
                    }
                }
            }


            // Mostrar el mensaje si existe
            mensajeFinal.value?.let { mensaje ->
                Text(
                    text = mensaje,
                    fontSize = 20.sp,
                    color = Color(0xFF0C180B),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }


        }
    }
}

// Función para obtener los puntos según la posición de la ficha
fun obtenerPuntosPorFicha(index: Int, tableroBase: List<List<Int>>): Int {
    val filas = tableroBase.size
    val columnas = tableroBase[0].size

    val fila = index / columnas
    val columna = index % columnas

    return when (tableroBase[fila][columna]) {
        2 -> 10
        3 -> 5
        4 -> 2
        else -> 0
    }
}
