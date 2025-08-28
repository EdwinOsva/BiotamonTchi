package com.example.biotamontchi.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PantallaPiedraPapelTijeras(
    audioViewModel: GameAudioViewModel2,
    onSalir: () -> Unit,
    prefs: PrefsManager,
    monedas: MutableState<Int>,

) {


    val config = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidth = with(density) { config.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { config.screenHeightDp.dp.toPx() }

    val fichasImages = listOf(
        R.drawable.ppt1, // Piedra
        R.drawable.ppt2, // Papel
        R.drawable.ppt3  // Tijeras
    )
// Genera 10 fichas aleatorias
    val listaBase = List(10) { fichasImages.random() }

// Genera la lista de ganadores correspondiente
    val listaGanadores = listaBase.map { fichaGanadora(it) }
    val listaBaseDesordenada = listaBase.shuffled()
    val listaGanadoresDesordenada = listaGanadores.shuffled()
    val fichasTablero = listaBaseDesordenada.mapIndexed { index, imagen ->
        Ficha(id = index, imagenRes = imagen)
    }
    val fichasAbajo = listaGanadoresDesordenada.take(6).mapIndexed { index, imagen ->
        Ficha(id = 100 + index, imagenRes = imagen)
    }
    val fichasArriba = listaGanadoresDesordenada.drop(6).take(4).mapIndexed { index, imagen ->
        Ficha(id = 200 + index, imagenRes = imagen)
    }
// aqui creabamos fichas con imagenes random, ahora,
// debemos asignar las 3 listas de imagenes anteriores a las correspondientes
    val fichasCentro = remember {
        mutableStateListOf(*fichasArriba.toTypedArray())
    }
    val fichasInferior = remember {
        mutableStateListOf(*fichasAbajo.toTypedArray())
    }

    // Definimos el camino rectangular en sentido antihorario
    val camino = buildList {
        // Esquinas
        val p1 = Offset(screenWidth * 0.82f, screenHeight * 0.1f)  // Derecha arriba
        val p2 = Offset(screenWidth * 0.1f, screenHeight * 0.1f)   // Izquierda arriba
        val p3 = Offset(screenWidth * 0.1f, screenHeight * 0.7f)   // Izquierda abajo
        val p4 = Offset(screenWidth * 0.82f, screenHeight * 0.7f)  // Derecha abajo

        // Agregar puntos entre p1 → p2 (arriba, 4 intermedios = 5 pasos)
        addAll(interpolarPuntos(p1, p2, 5))

        // Agregar puntos entre p2 → p3 (izquierda, 8 intermedios = 9 pasos)
        addAll(interpolarPuntos(p2, p3, 9))

        // Agregar puntos entre p3 → p4 (abajo, 4 intermedios = 5 pasos)
        addAll(interpolarPuntos(p3, p4, 5))

        // Agregar puntos entre p4 → p1 (derecha, 8 intermedios = 9 pasos)
        addAll(interpolarPuntos(p4, p1, 9))
    }
// Estado global para posiciones
    val posicionesFichas = remember { mutableStateListOf<Offset>() }
    repeat(10) { posicionesFichas.add(camino[0]) }


    // aqui hacemos listas para controlar cuales son y que hacen
    val fichasEnMovimientoCentro = remember { mutableStateListOf<FichaEnMovimiento>() }
    val fichasEnMovimientoInferior = remember { mutableStateListOf<FichaEnMovimiento>() }
//aqui donde estan
    val posicionesFichasCentro = remember { mutableStateMapOf<Int, Offset>() }
    val posicionesFichasInferior = remember { mutableStateMapOf<Int, Offset>() }
//aqui cuales hemos tocado para lanzar y se van a eliminar luego fuera de la pantalla
    val fichaAEliminarInferior = remember { mutableStateOf<Int?>(null) }
    val fichaAEliminarCentro = remember { mutableStateOf<Int?>(null) }
    val fichasGirando = remember {
        mutableStateListOf<FichaEnMovimiento>().apply {
            listaBaseDesordenada.forEachIndexed { index, imagen ->
                add(FichaEnMovimiento(id = index, imagenRes = imagen, x = camino[0].x, y = camino[0].y))
            }
        }
    }
    val mensajeResultado = remember { mutableStateOf<String?>(null) }
    val contadorFichasLanzadas = remember { mutableStateOf(0) }

    val mensajeFinal = remember { mutableStateOf<String?>(null) }
    val monedasGanadas = remember { mutableStateOf(0) }

    fun eliminarFichaCamino(ficha: FichaEnMovimiento) {
        val index = fichasGirando.indexOfFirst { it.id == ficha.id }
        if (index != -1) {
            fichasGirando[index] = fichasGirando[index].copy(activo = false)
        }
    }

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
            painter = painterResource(id = R.drawable.fondoppt),
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



        // Fichas fijas del centro (arriba)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                fichasCentro.forEach { ficha ->
                    Image(
                        painter = painterResource(id = ficha.imagenRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInRoot()
                                posicionesFichasCentro[ficha.id] = position
                            }
                            .clickable {
                                posicionesFichasCentro[ficha.id]?.let { offset ->
                                    contadorFichasLanzadas.value += 1
                                    audioViewModel.reproducirEfecto(R.raw.clic4) // sonido de click
                                    // Cuando la agregas
                                    fichasCentro.remove(ficha)
                                    fichasEnMovimientoCentro.add(
                                        FichaEnMovimiento(id = ficha.id, imagenRes = ficha.imagenRes, x = offset.x, y = offset.y)
                                    )


                                }
                            }
                    )
                }

            }
        }

        // Fichas fijas de abajo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                fichasInferior.forEach { ficha ->
                    Image(
                        painter = painterResource(id = ficha.imagenRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInRoot()
                                posicionesFichasInferior[ficha.id] = position
                            }
                            .clickable {
                                posicionesFichasInferior[ficha.id]?.let { offset ->
                                    contadorFichasLanzadas.value += 1
                                    audioViewModel.reproducirEfecto(R.raw.clic4) // sonido de click
                                    // Cuando la agregas
                                    fichasInferior.remove(ficha)
                                    fichasEnMovimientoInferior.add(
                                        FichaEnMovimiento(id = ficha.id, imagenRes = ficha.imagenRes, x = offset.x, y = offset.y)
                                    )


                                }
                            }
                    )
                }

            }
        }

        fichasEnMovimientoCentro.forEach { ficha ->
            key(ficha.id) {
                FichaAnimadaDesde(
                    audioViewModel = audioViewModel,
                    ficha = ficha,
                    fichasGirando = fichasGirando,
                    monedas = monedas,
                    monedasGanadas = monedasGanadas,
                    prefs = prefs,
                    onEliminarFichaCamino = { fichaGirando -> fichasGirando.removeIf { it.id == fichaGirando.id } },
                    onSalir = { fichasEnMovimientoCentro.remove(ficha) },
                    onMostrarMensaje = { texto -> mensajeResultado.value = texto },
                    velocidadFactor = 0.7f // 👈 más lento
                )
            }
        }

        fichasEnMovimientoInferior.forEach { ficha ->
            key(ficha.id) {
                FichaAnimadaDesde(
                    audioViewModel = audioViewModel,
                    ficha = ficha,
                    fichasGirando = fichasGirando,
                    monedas = monedas,
                    monedasGanadas = monedasGanadas,
                    prefs = prefs,
                    onEliminarFichaCamino = { fichaGirando -> fichasGirando.removeIf { it.id == fichaGirando.id } },
                    onSalir = { fichasEnMovimientoInferior.remove(ficha) },
                    onMostrarMensaje = { texto -> mensajeResultado.value = texto },
                    velocidadFactor = 1.0f // 👈 normal
                )
            }
        }

        fichasGirando.forEachIndexed { index, fichaGirando ->
            if (fichaGirando.activo) {
                FichaEnMovimiento(
                    index = index,
                    camino = camino,
                    imagenRes = fichaGirando.imagenRes,
                    delayMillis = index * 300L,
                    fichasGirando = fichasGirando
                )
            }
        }


        /*
                fichasGirando.forEachIndexed { index, fichaGirando ->
                    FichaEnMovimiento(
                        index = index,
                        camino = camino,
                        imagenRes = fichaGirando.imagenRes, // toma la imagen directamente de la ficha girando
                        delayMillis = index * 300L,
                        fichasGirando = fichasGirando
                    )
                }*/

        LaunchedEffect(mensajeResultado.value) {
            if (mensajeResultado.value != null) {
                delay(1500)
                mensajeResultado.value = null
            }
        }


        LaunchedEffect(fichaAEliminarInferior.value) {
            fichaAEliminarInferior.value?.let { id ->
                fichasEnMovimientoInferior.removeIf { it.id == id }
                fichaAEliminarInferior.value = null
            }
        }

        LaunchedEffect(fichaAEliminarCentro.value) {
            fichaAEliminarCentro.value?.let { id ->
                fichasEnMovimientoCentro.removeIf { it.id == id }
                fichaAEliminarCentro.value = null
            }
        }


// Luego observas ese contador
        LaunchedEffect(contadorFichasLanzadas.value) {
            if (contadorFichasLanzadas.value >= 10) {
                // Esperar que se vacíen las listas (las fichas terminen de moverse)
                while (fichasEnMovimientoCentro.isNotEmpty() || fichasEnMovimientoInferior.isNotEmpty()) {
                    delay(100)
                }
                mensajeFinal.value = "Fin del juego 🎉 ¡Ganaste ${monedasGanadas.value} monedas!"
                val ahora = System.currentTimeMillis()
                val nuevaFelicidad = (prefs.obtenerInt("feliz") + 5).coerceAtMost(10)
                prefs.guardarIndicador("feliz", nuevaFelicidad)
                prefs.guardarLong("fechaUltimaFelicidad", ahora)
                delay(3000)

                onSalir()
            }
        }

    }

    if (mensajeResultado.value != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mensajeResultado.value!!,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(16.dp)
            )
        }
    }
    // Mostrar el mensaje si existe
    mensajeFinal.value?.let { mensaje ->

        Box(
            modifier = Modifier.fillMaxSize(),

            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mensaje,
                fontSize = 30.sp,
                color = Color(0xFF83C0F5),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
            )
        }
    }

}
@Composable
fun FichaAnimadaDesde(
    audioViewModel: GameAudioViewModel2,
    ficha: FichaEnMovimiento,
    fichasGirando: List<FichaEnMovimiento>,
    monedas: MutableState<Int>,
    monedasGanadas: MutableState<Int>,
    prefs: PrefsManager,
    onEliminarFichaCamino: (FichaEnMovimiento) -> Unit,
    onSalir: () -> Unit,
    onMostrarMensaje: (String) -> Unit,
    velocidadFactor: Float = 1f // 👈 nuevo
) {
    val density = LocalDensity.current
    val sizePx = with(density) { 40.dp.toPx() }
    val yPos = remember { Animatable(ficha.y) }


    LaunchedEffect(Unit) {
        val distancia = ficha.y - (-100f)
        val velocidadPxPorMs = 1.0f * velocidadFactor // 👈 ajustamos la velocidad
        val duracion = (distancia / velocidadPxPorMs).toInt()

        val animJob = launch {
            yPos.animateTo(
                targetValue = -100f,
                animationSpec = tween(durationMillis = duracion, easing = LinearEasing)
            )
            onSalir()
        }

        snapshotFlow { yPos.value }.collect { currentY ->
            val choque = fichasGirando.firstOrNull { fichaCamino ->
                val rectLanzada = Rect(Offset(ficha.x, currentY), Size(sizePx, sizePx))
                val rectGirando = Rect(Offset(fichaCamino.x, fichaCamino.y), Size(sizePx, sizePx))
                rectLanzada.overlaps(rectGirando)
            }

            if (choque != null) {
                animJob.cancel()
                when (resultadoPPT(ficha.imagenRes, choque.imagenRes)) {
                    ResultadoPPT.GANAR -> {
                        monedas.value += 5
                        monedasGanadas.value += 5
                        audioViewModel.reproducirEfecto(R.raw.ganar_par)
                        prefs.guardarMonedas(monedas.value)

                        onEliminarFichaCamino(choque)
                        onMostrarMensaje("¡Ganaste!  :D")
                        onSalir()
                    }
                    ResultadoPPT.EMPATE -> {
                        monedas.value += 2
                        audioViewModel.reproducirEfecto(R.raw.clic3)
                        prefs.guardarMonedas(monedas.value)
                        onEliminarFichaCamino(choque)
                        onMostrarMensaje("Empate  :/")
                        onSalir()
                    }
                    ResultadoPPT.PERDER -> {
                        audioViewModel.reproducirEfecto(R.raw.clic5)
                        onMostrarMensaje("Perdiste  :(")
                        onSalir()
                    }
                }
            }
        }
    }

    Image(
        painter = painterResource(id = ficha.imagenRes),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer {
                translationX = ficha.x
                translationY = yPos.value
            }
    )
}




@Composable
fun FichaEnMovimiento(
    index: Int,
    camino: List<Offset>,
    imagenRes: Int,
    delayMillis: Long,
    fichasGirando: MutableList<FichaEnMovimiento>
) {
    val currentIndex = remember { mutableStateOf(0) }
    val offset = remember { Animatable(camino[0], Offset.VectorConverter) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        while (true) {
            val nextIndex = (currentIndex.value + 1) % camino.size
            val nextPoint = camino[nextIndex]

            val distancia = (nextPoint - offset.value).getDistance()
            val velocidadPxPorMs = 0.5f // ajusta este valor: px que recorre por milisegundo
            val duracion = (distancia / velocidadPxPorMs).toInt()

            offset.animateTo(
                targetValue = nextPoint,
                animationSpec = tween(durationMillis = duracion, easing = LinearEasing)
            )

            // Actualiza la posición en la lista global
            val fichaActualizada = fichasGirando.getOrNull(index)
            if (fichaActualizada != null) {

                fichasGirando[index] = fichaActualizada.copy(x = nextPoint.x, y = nextPoint.y)
            }

            currentIndex.value = nextIndex
            delay(1L)
        }
    }

    Image(
        painter = painterResource(id = imagenRes),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .offset {
                IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt())
            }
    )
}




fun interpolarPuntos(a: Offset, b: Offset, pasos: Int): List<Offset> {
    return List(pasos) { i ->
        val t = i / pasos.toFloat()
        Offset(
            x = a.x + (b.x - a.x) * t,
            y = a.y + (b.y - a.y) * t
        )
    }
}

data class FichaEnMovimiento(
    val id: Int,
    val imagenRes: Int,
    val x: Float,
    val y: Float,
    val activo: Boolean = true
)


data class Ficha(
    val id: Int,           // ID único
    val imagenRes: Int     // El recurso de imagen
)

// Función para sacar la que gana
fun fichaGanadora(imagen: Int): Int {
    return when (imagen) {
        R.drawable.ppt1 -> R.drawable.ppt2 //   Piedra
        R.drawable.ppt2 -> R.drawable.ppt3 // papel
        R.drawable.ppt3 -> R.drawable.ppt1 // tijeras
        else -> error("Ficha no reconocida")
    }
}
fun resultadoPPT(idLanzada: Int, idFija: Int): ResultadoPPT {
    return when {
        (idLanzada == R.drawable.ppt1 && idFija == R.drawable.ppt3) -> ResultadoPPT.GANAR
        (idLanzada == R.drawable.ppt2 && idFija == R.drawable.ppt1) -> ResultadoPPT.GANAR
        (idLanzada == R.drawable.ppt3 && idFija == R.drawable.ppt2) -> ResultadoPPT.GANAR

        (idLanzada == idFija) -> ResultadoPPT.EMPATE

        else -> ResultadoPPT.PERDER
    }
}

enum class ResultadoPPT { GANAR, EMPATE, PERDER }




