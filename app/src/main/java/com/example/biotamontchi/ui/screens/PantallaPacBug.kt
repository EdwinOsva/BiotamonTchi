package com.example.biotamontchi.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.biotamontchi.R
import com.example.biotamontchi.data.PrefsManager
import com.example.biotamontchi.model.CountdownTimer
import com.example.biotamontchi.model.Disparo
import com.example.biotamontchi.model.GameAudioViewModel2
import com.example.biotamontchi.model.Personaje
import com.example.biotamontchi.model.colocarObstaculo
import com.example.biotamontchi.model.disparar
import com.example.biotamontchi.model.imageMap
import com.example.biotamontchi.model.mapa
import com.example.biotamontchi.model.moverBlinky
import com.example.biotamontchi.model.moverClyde
import com.example.biotamontchi.model.moverFantasma
import com.example.biotamontchi.model.moverInky
import com.example.biotamontchi.model.moverPersonaje
import com.example.biotamontchi.model.moverPinky
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val tileSize = 32


var score:Int = 0
var enemigosEliminados:Int = 0
var posicionPremio = Pair(1, 20) // posición inicial

var vidasRestantes:Int = 3


@Composable
fun PantallaPacBug(
    audioViewModel: GameAudioViewModel2,
    onSalir: () -> Unit,
    prefs: PrefsManager,
    monedas: MutableState<Int>,
) {
    var mostrarGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
// Estado global en tu pantalla de juego
    var mensajeTemporal by remember { mutableStateOf<String?>(null) }


    val personajes = remember {
        mutableStateListOf(
            Personaje(Pair(5, 24), Pair(5, 24), Pair(5, 24)), // Fantasma 0
            Personaje(Pair(6, 24), Pair(6, 24), Pair(6, 24)), // Fantasma 1
            Personaje(Pair(5, 28), Pair(5, 28), Pair(5, 28)), // Fantasma 2
            Personaje(Pair(6, 28), Pair(6, 28), Pair(6, 28)), // Fantasma 3
            Personaje(Pair(2, 1), Pair(2, 1), Pair(2, 1)), // Fantasma 4
            Personaje(Pair(3, 1), Pair(3, 1), Pair(3, 1)), // Fantasma 5
            Personaje(Pair(4, 1), Pair(4, 1), Pair(4, 1)), // Fantasma 6
            Personaje(Pair(5, 1), Pair(5, 1), Pair(5, 1)) // Fantasma 7
        )
    }

    val animaciones = personajes.map { personaje ->
        animateFloatAsState(
            targetValue = personaje.destino.first * tileSize.toFloat(),
            animationSpec = tween(durationMillis = 500)
        ) to animateFloatAsState(
            targetValue = personaje.destino.second * tileSize.toFloat(),
            animationSpec = tween(durationMillis = 500)
        )
    }

    // Paso 3: Cargar las imágenes
    val imageBitmaps = imageMap.mapValues { entry ->
        ImageBitmap.imageResource(id = entry.value)
    }


    val personajeBitmap = ImageBitmap.imageResource(R.drawable.insecto02)
    val personajeBitmap2 = ImageBitmap.imageResource(R.drawable.insecto)
    var posicionPersonaje by remember {
        mutableStateOf(Pair(1, 41)) // Posición inicial
    }
    var destinoPersonaje by remember { mutableStateOf(Pair(1, 41)) }

    var frameIndex by remember { mutableStateOf(0) } // Índice del sprite
    var direccion by remember { mutableStateOf(0) } // Dirección actual

    // Variable para la animación
    val tiempoAnimacion = 400
    val tiempoAnimacion2 = 800
    var enMovimiento by remember { mutableStateOf(false) }
    var direccionActual by remember { mutableStateOf(-1) }

    val animatedX by animateFloatAsState(
        targetValue = destinoPersonaje.first * tileSize.toFloat(),
        animationSpec = tween(durationMillis = 200)
    )

    val animatedY by animateFloatAsState(
        targetValue = destinoPersonaje.second * tileSize.toFloat(),
        animationSpec = tween(durationMillis = 200)
    )

    var obstaculos by remember { mutableStateOf(mutableListOf<Pair<Int, Int>>()) }
    val obstaculoBitmap = ImageBitmap.imageResource(R.drawable.l20) // Imagen del obstáculo


    val disparos = remember { mutableStateListOf<Disparo>() }

    fun ImageBitmap.resize(width: Int, height: Int): ImageBitmap {
        return Bitmap.createScaledBitmap(this.asAndroidBitmap(), width, height, true)
            .asImageBitmap()
    }

    val disparoBitmap0 = ImageBitmap.imageResource(id = R.drawable.disparo00)
    val disparoBitmap1 = ImageBitmap.imageResource(id = R.drawable.disparo01)
    val disparoBitmap2 = ImageBitmap.imageResource(id = R.drawable.disparo02)

    val explotaBitmap0 = ImageBitmap.imageResource(id = R.drawable.explota00)
    val explotaBitmap1 = ImageBitmap.imageResource(id = R.drawable.explota01)
    val explotaBitmap2 = ImageBitmap.imageResource(id = R.drawable.explota02)

    val scope = rememberCoroutineScope()


    var mostrarPremio by remember { mutableStateOf(false) }

    var valorPremio by remember { mutableStateOf(0) }

    val regalo01 = ImageBitmap.imageResource(id = R.drawable.regalo01)
    val regalo02 = ImageBitmap.imageResource(id = R.drawable.regalo02)
    val regalo03 = ImageBitmap.imageResource(id = R.drawable.regalo03)
    val regalo04 = ImageBitmap.imageResource(id = R.drawable.regalo04)
    val regalo11 = ImageBitmap.imageResource(id = R.drawable.regalo10)
    val regalo12 = ImageBitmap.imageResource(id = R.drawable.regalo11)
    val regalo13 = ImageBitmap.imageResource(id = R.drawable.regalo12)
    val regalo14 = ImageBitmap.imageResource(id = R.drawable.regalo13)
    val regalo21 = ImageBitmap.imageResource(id = R.drawable.regalo21)
    val regalo22 = ImageBitmap.imageResource(id = R.drawable.regalo22)
    val regalo23 = ImageBitmap.imageResource(id = R.drawable.regalo23)
    val regalo24 = ImageBitmap.imageResource(id = R.drawable.regalo24)
    val regalo31 = ImageBitmap.imageResource(id = R.drawable.regalo31)
    val regalo32 = ImageBitmap.imageResource(id = R.drawable.regalo32)
    val regalo33 = ImageBitmap.imageResource(id = R.drawable.regalo33)
    val regalo34 = ImageBitmap.imageResource(id = R.drawable.regalo34)
    var animacionPremioActual by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }
    var cuadroActual by remember { mutableStateOf(0) }

    val posicionesPremio = listOf(
        Pair(1, 20),
        Pair(9, 2),
        Pair(11, 17),
        Pair(8, 20),
        Pair(3, 30),
        Pair(17, 35)
    )

    val posicionesPremio2 = listOf(
        Pair(1, 20),
        Pair(9, 2),
        Pair(11, 17),
        Pair(8, 20),
        Pair(3, 30),
        Pair(17, 35)
    )
    var posicionPistola by remember { mutableStateOf(posicionesPremio2.random()) }
// Control de balas

    val arma01 = ImageBitmap.imageResource(id = R.drawable.arma01)
    val arma02 = ImageBitmap.imageResource(id = R.drawable.arma02)
    val arma03 = ImageBitmap.imageResource(id = R.drawable.arma03)
    val arma04 = ImageBitmap.imageResource(id = R.drawable.arma04)
// Pistola (nuevo premio)
    val pistolaFrames = listOf(arma01, arma02, arma03, arma04) // tus imágenes
    var mostrarPistola by remember { mutableStateOf(true) }

    var cuadroActualPistola by remember { mutableStateOf(0) }

    var balasRestantes by remember { mutableStateOf(0) }
    var obstaculosRestantes = remember { mutableStateOf(10) }

    val obstaculosMax = 20 // máximo que puede tener
    val comiBitmaps = listOf( ImageBitmap.imageResource(R.drawable.comi1), ImageBitmap.imageResource(R.drawable.comi2), ImageBitmap.imageResource(R.drawable.comi3), ImageBitmap.imageResource(R.drawable.comi4), ImageBitmap.imageResource(R.drawable.comi5), ImageBitmap.imageResource(R.drawable.comi6), ImageBitmap.imageResource(R.drawable.comi7), ImageBitmap.imageResource(R.drawable.comi8), ImageBitmap.imageResource(R.drawable.comi9), ImageBitmap.imageResource(R.drawable.comi10), ImageBitmap.imageResource(R.drawable.comi11), ImageBitmap.imageResource(R.drawable.comi12), ImageBitmap.imageResource(R.drawable.comi13) )

    val frutasRecogidas = remember { MutableList(13) { false } }
    val posicionesFrutas = remember {
        val libres = mutableListOf<Pair<Int, Int>>()
        for (y in mapa.indices) {
            for (x in mapa[y].indices) {
                if (mapa[y][x] == 0) libres.add(Pair(x, y))
            }
        }
        libres.shuffle()
        libres.take(13).toMutableList()
    }
    val puntosFruta = remember { List(13) { 50 } }

    LaunchedEffect(Unit) {  // Se ejecuta solo al inicio
        mensajeTemporal = "Inicio del juego.\nCorre!\nToma todas las frutas y gana premios.\nA-Disparas D-Pon obstáculos"
        delay(1200L) // Mostrar 1 segundo
        mensajeTemporal = null
    }
    LaunchedEffect(mostrarGameOver) {
        if (mostrarGameOver) {
            // Monedas por score
            val monedasGanadas = score / 100
            monedas.value += monedasGanadas
            prefs.guardarMonedas(monedas.value)
            if (monedasGanadas!= 0) {
                prefs.sumarPuntos(monedasGanadas) // +10 puntos, por moneda
            }
            // Aumentar felicidad
            val ahora = System.currentTimeMillis()
            val nuevaFelicidad = (prefs.obtenerInt("feliz") + 5).coerceAtMost(10)
            prefs.guardarIndicador("feliz", nuevaFelicidad)
            prefs.guardarLong("fechaUltimaFelicidad", ahora)


            delay(3000)
            score  = 0
            enemigosEliminados  = 0
            vidasRestantes  = 3

            onSalir()
        }
    }

    /*
    LaunchedEffect(posicionPersonaje, personajes) {
        personajes.forEach { fantasma ->
            if (!fantasma.destruido) {
                if (posicionPersonaje == fantasma.destino) {
                    // Colisión detectada
                    vidasRestantes--        // Reducir vidas
                    if (vidasRestantes <= 0) {
                        mostrarGameOver = true
                    }
                    // Reiniciar posición del personaje si quieres
                    posicionPersonaje = Pair(1, 41)
                    destinoPersonaje = Pair(1, 41)

                    // Marcar que el fantasma fue tocado (opcional)
                    fantasma.tocado = true
                }
            }
        }
    }*/
/*
    LaunchedEffect(posicionPersonaje, personajes, isPaused) {
        if (!isPaused) {
            personajes.forEach { fantasma ->
                if (!fantasma.destruido) {
                    val colisionPorPosicion = (posicionPersonaje == fantasma.posicion)
                    val colisionPorDestino = (posicionPersonaje == fantasma.destino)

                    if (colisionPorPosicion || colisionPorDestino) {
                        // Colisión detectada
                        vidasRestantes = (vidasRestantes - 1).coerceAtLeast(0)
                        mensajeTemporal = "¡Moriste!"


                        if (vidasRestantes <= 0) {
                            mostrarGameOver = true
                        }

                        // Reiniciar posición del personaje
                        posicionPersonaje = Pair(1, 41)
                        destinoPersonaje = Pair(1, 41)

                        // Marcar fantasma como tocado (opcional)
                        fantasma.tocado = true
                    }
                }
            }
        }
    }*/
    LaunchedEffect(Unit) {
        while (true) {
            delay(100) // cada 0.1s
            if (!isPaused) {
                personajes.forEach { fantasma ->
                    if (!fantasma.destruido) {
                        // 🔹 Evaluar solo si está dentro de un rango cercano
                        val dx = kotlin.math.abs(posicionPersonaje.first - fantasma.posicion.first)
                        val dy = kotlin.math.abs(posicionPersonaje.second - fantasma.posicion.second)
                        val rangoChequeo = 1 // 1 tile alrededor

                        if (dx <= rangoChequeo && dy <= rangoChequeo) {
                            // Evaluar colisión real
                            val colision =
                                posicionPersonaje == fantasma.posicion ||
                                        posicionPersonaje == fantasma.destino ||
                                        destinoPersonaje == fantasma.posicion ||
                                        destinoPersonaje == fantasma.destino

                            if (colision) {
                                vidasRestantes = (vidasRestantes - 1).coerceAtLeast(0)
                                mensajeTemporal = "¡Moriste!"

                                if (vidasRestantes <= 0) {
                                    mostrarGameOver = true
                                }

                                // Reiniciar posición del jugador
                                posicionPersonaje = Pair(1, 41)
                                destinoPersonaje = Pair(1, 41)

                                fantasma.tocado = true
                            }
                        }
                    }
                }
            }
        }
    }



    LaunchedEffect(mensajeTemporal) {
        if (mensajeTemporal != null) {
            delay(1000) // 1 segundo
            mensajeTemporal = null
        }
    }

    LaunchedEffect(score) {

        if (score > 0 && score % 1000 == 0) {
            vidasRestantes++
        }
    }

    LaunchedEffect(mostrarPistola) {
        while (mostrarPistola && pistolaFrames.isNotEmpty()) {
            delay(150L)
            cuadroActualPistola = (cuadroActualPistola + 1) % pistolaFrames.size
        }
    }

/*
    LaunchedEffect(disparos) {
        while (true) {
            delay(100)

            val iterator = disparos.iterator()
            while (iterator.hasNext()) {
                val disparo = iterator.next()

                if (!disparo.activo) {
                    if (disparo.enExplosion) {
                        if (disparo.explosionFrame < 2) {
                            disparo.explosionFrame++
                        } else {
                            iterator.remove()
                        }
                    } else {
                        iterator.remove()
                    }
                    continue
                }

                if (disparo.frame < 2) {
                    disparo.frame++
                    continue
                }

                val siguienteX = when (disparo.direccion) {
                    0 -> disparo.x.toInt()
                    1 -> disparo.x.toInt()
                    2 -> disparo.x.toInt() - tileSize
                    3 -> disparo.x.toInt() + tileSize
                    else -> disparo.x.toInt()
                }
                val siguienteY = when (disparo.direccion) {
                    0 -> disparo.y.toInt() + tileSize
                    1 -> disparo.y.toInt() - tileSize
                    2 -> disparo.y.toInt()
                    3 -> disparo.y.toInt()
                    else -> disparo.y.toInt()
                }

                val mapX = siguienteX / tileSize
                val mapY = siguienteY / tileSize
                val celdaDisparo = Pair(mapX, mapY)

                if (mapX in mapa[0].indices && mapY in mapa.indices) {
                    if (mapa[mapY][mapX] == 0) {
                        // Comprobar si hay un personaje en esa casilla
                        val personajeEnCasilla = personajes.firstOrNull {
                            it.posicion == celdaDisparo
                        }

                        if (personajeEnCasilla != null) {
                            // Impacta a personaje
                            disparo.activo = false
                            disparo.enExplosion = true
                            disparo.explosionFrame = 0
                            // Aquí puedes eliminar al personaje o marcarlo
                            // Eliminar personaje del mapa

                            personajeEnCasilla.destruido = true

                            score += 10
                            enemigosEliminados++

                            if (enemigosEliminados % 10 == 0 && !mostrarPremio) {
                                val opciones = listOf(
                                    Pair(listOf(regalo01, regalo02, regalo03, regalo04), 10),
                                    Pair(listOf(regalo11, regalo12, regalo13, regalo14), 100),
                                    Pair(listOf(regalo21, regalo22, regalo23, regalo24), 1000),
                                    Pair(listOf(regalo31, regalo32, regalo33, regalo34), 10000)
                                )
                                posicionPremio = posicionesPremio.random()
                                val (animacionSeleccionada, valor) = opciones.random()
                                animacionPremioActual = animacionSeleccionada
                                // Esta es la nueva posición del premio
                                valorPremio = valor
                                mostrarPremio = true
                                enemigosEliminados = 0

                            }


                            // Volver a agregarlo después de 10 segundos en su posición original
                            scope.launch {

                                personajeEnCasilla.destruido = false
                                personajeEnCasilla.posicion = personajeEnCasilla.posicionOriginal
                                personajeEnCasilla.destino = personajeEnCasilla.posicionOriginal
                                personajeEnCasilla.enMovimiento = true
                            }
                        }
                        // Comprobar si hay un obstáculo en esa casilla
                        else if (obstaculos.contains(celdaDisparo)) {
                            disparo.activo = false
                            disparo.enExplosion = true
                            disparo.explosionFrame = 0

                            // Eliminar el obstáculo
                            obstaculos = obstaculos.toMutableList().apply {
                                remove(celdaDisparo)
                                score += 1
                            }
                        } else {
                            // Avanzar el disparo si no impacta nada
                            when (disparo.direccion) {
                                0 -> disparo.y += tileSize
                                1 -> disparo.y -= tileSize
                                2 -> disparo.x -= tileSize
                                3 -> disparo.x += tileSize
                            }
                        }
                    } else {
                        // Impacta contra pared (mapa != 0)
                        disparo.activo = false
                        disparo.enExplosion = true
                        disparo.explosionFrame = 0
                    }
                } else {
                    // Fuera del mapa
                    disparo.activo = false
                }
            }
        }
    }*/
// ✅ Versión con soporte de pausa
    LaunchedEffect(disparos, isPaused) {
        while (true) {
            delay(100)

            // 🔹 Si está en pausa, saltamos la iteración y esperamos
            if (isPaused) continue

            val iterator = disparos.iterator()
            while (iterator.hasNext()) {
                val disparo = iterator.next()

                if (!disparo.activo) {
                    if (disparo.enExplosion) {
                        if (disparo.explosionFrame < 2) {
                            disparo.explosionFrame++
                        } else {
                            iterator.remove()
                        }
                    } else {
                        iterator.remove()
                    }
                    continue
                }

                if (disparo.frame < 2) {
                    disparo.frame++
                    continue
                }

                val siguienteX = when (disparo.direccion) {
                    0 -> disparo.x.toInt()
                    1 -> disparo.x.toInt()
                    2 -> disparo.x.toInt() - tileSize
                    3 -> disparo.x.toInt() + tileSize
                    else -> disparo.x.toInt()
                }
                val siguienteY = when (disparo.direccion) {
                    0 -> disparo.y.toInt() + tileSize
                    1 -> disparo.y.toInt() - tileSize
                    2 -> disparo.y.toInt()
                    3 -> disparo.y.toInt()
                    else -> disparo.y.toInt()
                }

                val mapX = siguienteX / tileSize
                val mapY = siguienteY / tileSize
                val celdaDisparo = Pair(mapX, mapY)

                if (mapX in mapa[0].indices && mapY in mapa.indices) {
                    if (mapa[mapY][mapX] == 0) {
                        val personajeEnCasilla = personajes.firstOrNull {
                            it.posicion == celdaDisparo
                        }

                        if (personajeEnCasilla != null) {
                            disparo.activo = false
                            disparo.enExplosion = true
                            disparo.explosionFrame = 0

                            personajeEnCasilla.destruido = true

                            score += 10
                            enemigosEliminados++

                            if (enemigosEliminados % 10 == 0 && !mostrarPremio) {
                                val opciones = listOf(
                                    Pair(listOf(regalo01, regalo02, regalo03, regalo04), 10),
                                    Pair(listOf(regalo11, regalo12, regalo13, regalo14), 100),
                                    Pair(listOf(regalo21, regalo22, regalo23, regalo24), 1000),
                                    Pair(listOf(regalo31, regalo32, regalo33, regalo34), 10000)
                                )
                                posicionPremio = posicionesPremio.random()
                                val (animacionSeleccionada, valor) = opciones.random()
                                animacionPremioActual = animacionSeleccionada
                                valorPremio = valor
                                mostrarPremio = true
                                enemigosEliminados = 0
                            }

                            // Volver a agregarlo después de 10 segundos
                            scope.launch {
                                personajeEnCasilla.destruido = false
                                personajeEnCasilla.posicion = personajeEnCasilla.posicionOriginal
                                personajeEnCasilla.destino = personajeEnCasilla.posicionOriginal
                                personajeEnCasilla.enMovimiento = true
                            }
                        } else if (obstaculos.contains(celdaDisparo)) {
                            disparo.activo = false
                            disparo.enExplosion = true
                            disparo.explosionFrame = 0

                            obstaculos = obstaculos.toMutableList().apply {
                                remove(celdaDisparo)
                                score += 1
                            }
                        } else {
                            when (disparo.direccion) {
                                0 -> disparo.y += tileSize
                                1 -> disparo.y -= tileSize
                                2 -> disparo.x -= tileSize
                                3 -> disparo.x += tileSize
                            }
                        }
                    } else {
                        disparo.activo = false
                        disparo.enExplosion = true
                        disparo.explosionFrame = 0
                    }
                } else {
                    disparo.activo = false
                }
            }
        }
    }



    LaunchedEffect(mostrarPremio) {
        while (mostrarPremio && animacionPremioActual.isNotEmpty()) {
            delay(150L)
            val frameCount = animacionPremioActual.size
            if (frameCount > 0) {
                cuadroActual = (cuadroActual + 1) % frameCount
            }
        }
    }


/*
    personajes.forEachIndexed { index, _ ->
        LaunchedEffect(Unit) {
            while (true) {
                delay((700..900).random().toLong()) // Variar la velocidad
                val personaje = personajes.getOrNull(index) ?: return@LaunchedEffect
                if (personaje.destruido) return@LaunchedEffect
                when (index) {
                    0 -> moverBlinky(
                        index,
                        personajes,
                        posicionPersonaje,
                        direccionActual,
                        obstaculos
                    ) // Blinky (Rojo)
                    1 -> moverPinky(
                        index,
                        personajes,
                        posicionPersonaje,
                        direccionActual,
                        obstaculos
                    )  // Pinky (Rosa)
                    2 -> moverInky(
                        index,
                        personajes,
                        posicionPersonaje,
                        direccionActual,
                        direccion,
                        obstaculos
                    )   // Inky (Azul)
                    3 -> moverClyde(
                        index,
                        personajes,
                        posicionPersonaje,
                        direccionActual,
                        obstaculos
                    )  // Clyde (Naranja)
                    else -> moverFantasma(personajes, index, posicionPersonaje, obstaculos, mapa)
                }

                // Agregar más casos si tienes más fantasmas, o puedes hacer uno genérico si todos siguen la misma lógica
            }
        }
    }*/

// Movimiento de los personajes
    personajes.forEachIndexed { index, _ ->
        LaunchedEffect(Unit) {
            while (true) {
                delay((700..900).random().toLong()) // Variar la velocidad

                if (isPaused) {
                    // Mientras esté en pausa, espera hasta que se reanude
                    while (isPaused) {
                        delay(100)
                    }
                }

                val personaje = personajes.getOrNull(index) ?: return@LaunchedEffect
                if (personaje.destruido) return@LaunchedEffect

                when (index) {
                    0 -> moverBlinky(index, personajes, posicionPersonaje, direccionActual, obstaculos)
                    1 -> moverPinky(index, personajes, posicionPersonaje, direccionActual, obstaculos)
                    2 -> moverInky(index, personajes, posicionPersonaje, direccionActual, direccion, obstaculos)
                    3 -> moverClyde(index, personajes, posicionPersonaje, direccionActual, obstaculos)
                    else -> moverFantasma(personajes, index, posicionPersonaje, obstaculos, mapa)
                }
            }
        }
    }

/*
// Animación para avanzar por los cuadros del sprite
    personajes.forEachIndexed { index, _ ->
        LaunchedEffect(personajes[index].direccion) {
            while (true) {
                delay(tiempoAnimacion2.toLong())
                personajes[index] = personajes[index].copy(
                    frameIndex = (personajes[index].frameIndex % 6) + 1 // Cambio de cuadro de animación
                )
            }
        }
    }*/
    // Animación para avanzar por los cuadros del sprite
    personajes.forEachIndexed { index, _ ->
        LaunchedEffect(personajes[index].direccion) {
            while (true) {
                delay(tiempoAnimacion2.toLong())

                if (isPaused) {
                    // Mientras esté en pausa, espera hasta que se reanude
                    while (isPaused) {
                        delay(100)
                    }
                }

                personajes[index] = personajes[index].copy(
                    frameIndex = (personajes[index].frameIndex % 6) + 1 // Cambio de cuadro de animación
                )
            }
        }
    }

/*
    LaunchedEffect(direccionActual) {
        while (enMovimiento) {
            delay(300)  // Ajusta la velocidad de movimiento

            val nuevaPosicion = when (direccionActual) {
                0 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, 0, 1)  // Abajo
                1 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, 0, -1) // Arriba
                2 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, -1, 0) // Izquierda
                3 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, 1, 0)  // Derecha
                else -> posicionPersonaje
            }

            // Actualizamos los estados de la UI
            posicionPersonaje = nuevaPosicion
            destinoPersonaje = nuevaPosicion
        }
    }*/


    LaunchedEffect(direccionActual) {
        while (enMovimiento) {
            delay(300)  // Ajusta la velocidad de movimiento

            if (isPaused) {
                // Mientras esté en pausa, espera
                while (isPaused) {
                    delay(100)
                }
            }

            val nuevaPosicion = when (direccionActual) {
                0 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, 0, 1)  // Abajo
                1 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, 0, -1) // Arriba
                2 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, -1, 0) // Izquierda
                3 -> moverPersonaje(mapa, obstaculos, posicionPersonaje, 1, 0)  // Derecha
                else -> posicionPersonaje
            }

            // Actualizamos los estados de la UI
            posicionPersonaje = nuevaPosicion
            destinoPersonaje = nuevaPosicion
        }
    }


/*
    // Animación: Avanzar por los cuadros del dibujo del personaje segun su direccion
    LaunchedEffect(direccion) {
        while (true) {
            delay(tiempoAnimacion.toLong())
            frameIndex = (frameIndex % 6) + 1  // Cambio de cuadro
        }
    }*/


    // Animación: Avanzar por los cuadros del dibujo del personaje segun su direccion
    LaunchedEffect(direccion) {
        while (true) {
            delay(tiempoAnimacion.toLong())

            if (isPaused) {
                // Mientras esté en pausa, no avances los frames
                while (isPaused) {
                    delay(100)
                }
            }

            frameIndex = (frameIndex % 6) + 1  // Cambio de cuadro
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // 🔥 Fondo sólido que tapa la pantalla anterior
            .pointerInput(Unit) {
                // 🔒 Bloquea los toques al fondo
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Dibujo del mapa y personaje
        Canvas(
            modifier = Modifier.size(
                width = (mapa[0].size * tileSize).dp,
                height = (mapa.size * tileSize).dp
            )
        ) {

            // Dibujar mapa
            for (rowIndex in mapa.indices) {
                for (colIndex in mapa[rowIndex].indices) {
                    val tileNumber = mapa[rowIndex][colIndex]
                    imageBitmaps[tileNumber]?.let {
                        drawImage(
                            image = it,
                            topLeft = Offset(
                                x = (colIndex * tileSize).toFloat(),
                                y = (rowIndex * tileSize).toFloat()
                            )
                        )
                    }
                }
            }
//premio municion
            if (mostrarPistola && pistolaFrames.isNotEmpty()) {
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint()

                    val srcSize = 64
                    val targetSize = 32
                    val (x, y) = posicionPistola
                    val srcRect = android.graphics.Rect(0, 0, srcSize, srcSize)
                    val destRect = android.graphics.Rect(
                        x * tileSize,
                        y * tileSize,
                        x * tileSize + targetSize,
                        y * tileSize + targetSize
                    )

                    val frame = pistolaFrames[cuadroActualPistola]
                    canvas.nativeCanvas.drawBitmap(
                        frame.asAndroidBitmap(),
                        srcRect,
                        destRect,
                        paint
                    )
                }

                if (posicionPersonaje == posicionPistola) {
                    mostrarPistola = false
                    balasRestantes += 50 // o el número que quieras de recarga
                    cuadroActualPistola = 0
                    posicionPistola = posicionesPremio.random()
                }

            }
//cajas premio

            if (mostrarPremio && animacionPremioActual.isNotEmpty()) {
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint()

                    val srcSize = 64
                    val targetSize = 32

                    val (x, y) = posicionPremio
                    val srcRect = android.graphics.Rect(0, 0, srcSize, srcSize)
                    val destRect = android.graphics.Rect(
                        x * tileSize,
                        y * tileSize,
                        x * tileSize + targetSize,
                        y * tileSize + targetSize
                    )

                    val frame = animacionPremioActual[cuadroActual]

                    canvas.nativeCanvas.drawBitmap(
                        frame.asAndroidBitmap(),
                        srcRect,
                        destRect,
                        paint
                    )
                }

                if (posicionPersonaje == posicionPremio) {
                    score += valorPremio
                    mostrarPremio = false
                    animacionPremioActual = emptyList()
                    cuadroActual = 0
                    valorPremio = 0

                    // Recargar obstáculos, sin superar el máximo
                    obstaculosRestantes.value = (obstaculosRestantes.value + 5).coerceAtMost(obstaculosMax)

                }

            }


            posicionesFrutas.forEachIndexed { index, pos ->
                if (!frutasRecogidas[index]) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint()
                        val targetSize = 32

                        val (x, y) = pos
                        val srcRect = android.graphics.Rect(0, 0, comiBitmaps[index].width, comiBitmaps[index].height)
                        val destRect = android.graphics.Rect(
                            x * tileSize,
                            y * tileSize,
                            x * tileSize + targetSize,
                            y * tileSize + targetSize
                        )

                        canvas.nativeCanvas.drawBitmap(
                            comiBitmaps[index].asAndroidBitmap(),
                            srcRect,
                            destRect,
                            paint
                        )
                    }

                    // Detectar si el personaje recoge la fruta
                    if (posicionPersonaje == pos) {
                        score += puntosFruta[index]
                        frutasRecogidas[index] = true
                    }
                }
            }

// Si todas las frutas fueron recogidas
            if (frutasRecogidas.all { it }) {
                mensajeTemporal = "¡Misión cumplida!"
            }





//disparo
            for (disparo in disparos) {

                // Si está en modo explosión, elegir el bitmap correspondiente
                val bitmap = if (disparo.enExplosion) {
                    when (disparo.explosionFrame) {
                        0 -> explotaBitmap0
                        1 -> explotaBitmap1
                        2 -> explotaBitmap2
                        else -> null
                    }
                } else {
                    when (disparo.frame) {
                        0 -> disparoBitmap0
                        1 -> disparoBitmap1
                        else -> disparoBitmap2
                    }
                }
//personaje principal
                // Solo dibujar si el bitmap no es nulo (por seguridad)
                if (bitmap != null) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint()

                        // Tamaño original del bitmap (64x64)
                        val srcSize = 64
                        // Tamaño al que lo quieres reescalar (32x32)
                        // Tamaño al que lo quieres reescalar
                        val targetSize = if (disparo.enExplosion) 64 else 32

                        val srcRect = android.graphics.Rect(0, 0, srcSize, srcSize)
                        val destRect = android.graphics.Rect(
                            disparo.x.toInt(),
                            disparo.y.toInt(),
                            disparo.x.toInt() + targetSize,
                            disparo.y.toInt() + targetSize,
                        )

                        canvas.save()

                        // Si NO está explotando, aplicar rotación
                        if (!disparo.enExplosion) {
                            val centerX = disparo.x + targetSize / 2
                            val centerY = disparo.y + targetSize / 2
                            val angle = when (disparo.direccion) {
                                0 -> 180f
                                1 -> 0f
                                2 -> 270f
                                3 -> 90f
                                else -> 0f
                            }
                            canvas.rotate(angle, centerX, centerY)
                        }

                        // Dibuja el bitmap
                        canvas.nativeCanvas.drawBitmap(
                            bitmap.asAndroidBitmap(),
                            srcRect,
                            destRect,
                            paint
                        )

                        canvas.restore()
                    }
                }
            }


//obstaculo
            for (obstaculo in obstaculos) {
                drawImage(
                    image = obstaculoBitmap,
                    topLeft = Offset(
                        x = (obstaculo.first * tileSize).toFloat(),
                        y = (obstaculo.second * tileSize).toFloat()
                    )
                )
            }
            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint()
                val spriteSize = 32
                val frameX = frameIndex * spriteSize
                val frameY = direccion * spriteSize
                val srcRect =
                    android.graphics.Rect(frameX, frameY, frameX + spriteSize, frameY + spriteSize)
                val destRect = android.graphics.Rect(
                    animatedX.toInt(),
                    animatedY.toInt(),
                    animatedX.toInt() + spriteSize,
                    animatedY.toInt() + spriteSize,
                )
                canvas.nativeCanvas.drawBitmap(
                    personajeBitmap.asAndroidBitmap(),
                    srcRect,
                    destRect,
                    paint
                )
            }
//fantasmas
            for ((index, personaje) in personajes.withIndex()) {

                val (animatedX, animatedY) = animaciones[index]
                if (personaje.destruido) continue
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint()
                    val spriteSize = 32
                    val frameX = frameIndex * spriteSize
                    val frameY = personaje.direccion * spriteSize
                    val srcRect =
                        android.graphics.Rect(
                            frameX,
                            frameY,
                            frameX + spriteSize,
                            frameY + spriteSize
                        )
                    val destRect = android.graphics.Rect(
                        animatedX.value.toInt(),
                        animatedY.value.toInt(),
                        animatedX.value.toInt() + spriteSize,
                        animatedY.value.toInt() + spriteSize,
                    )

                    canvas.nativeCanvas.drawBitmap(
                        personajeBitmap2.asAndroidBitmap(),
                        srcRect,
                        destRect,
                        paint
                    )
                }
            }
        }

        if (mostrarGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¡Fin del juego!\nScore: $score\nMonedas +${score/100}",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Button(
                        onClick = { isPaused = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("Reanudar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            mostrarGameOver= true
                            isPaused = false
                                                 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Game Over", color = Color.White)
                    }




                }
            }
        }
// Para mostrar el mensaje flotante en pantalla
        if (mensajeTemporal != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mensajeTemporal!!,
                    color = Color.Red,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(12.dp)
                )
            }
        }


        Box(
            modifier = Modifier

                .padding(8.dp)
                .zIndex(1f) // 🔥 Esto lo coloca al frente
                .background(Color.Black.copy(alpha = 0.2f)),
            contentAlignment = Alignment.TopCenter// Fondo semi-transparente opcional
        ) {
            CountdownTimer(
                onTimeUp = { mostrarGameOver = true },
                isPaused = isPaused, // 🔹 aquí pasas tu flag de pausa
                onWarning = {
                    mensajeTemporal = "⚠️ ¡Quedan 20 segundos!"
                }
            )
                 { time ->
                GameHeader(
                    timeLeft = time,
                    score = score.toString(),

                    mission = "Mision:1-Bichos",
                    vidas = vidasRestantes,
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(18.dp)) // 🔹 Espacio vertical de 12dp
                    Text(
                        text = "Balas: $balasRestantes",
                        fontSize = 12.sp,
                        color = Color.Yellow,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "Obstaculos: ${obstaculosRestantes.value}",
                        fontSize = 12.sp,
                        color = Color.Yellow,
                        modifier = Modifier.padding(8.dp)
                    )

                }

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Columna de controles de dirección
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = {
                                    moverPersonaje(mapa, obstaculos, posicionPersonaje, 0, -1)
                                    direccion = 1  // Arriba
                                    direccionActual = 1
                                    enMovimiento = true
                                },
                                modifier = Modifier.border(2.dp, Color.White.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White.copy(alpha = 0.5f)
                                )
                            ) { Text("⬆") }

                            Row {
                                Button(
                                    onClick = {
                                        moverPersonaje(mapa, obstaculos, posicionPersonaje, -1, 0)
                                        direccion = 2  // Izquierda
                                        direccionActual = 2
                                        enMovimiento = true
                                    },
                                    modifier = Modifier.border(
                                        2.dp,
                                        Color.White.copy(alpha = 0.5f)
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.White.copy(alpha = 0.5f)
                                    )
                                ) { Text("⬅") }

                                Spacer(modifier = Modifier.width(10.dp))

                                Button(
                                    onClick = {
                                        moverPersonaje(mapa, obstaculos, posicionPersonaje, 1, 0)
                                        direccion = 3  // Derecha
                                        direccionActual = 3
                                        enMovimiento = true
                                    },
                                    modifier = Modifier.border(
                                        2.dp,
                                        Color.White.copy(alpha = 0.5f)
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.White.copy(alpha = 0.5f)
                                    )
                                ) { Text("➡") }
                            }

                            Button(
                                onClick = {
                                    moverPersonaje(mapa, obstaculos, posicionPersonaje, 0, 1)
                                    direccion = 0  // Abajo
                                    direccionActual = 0
                                    enMovimiento = true
                                },
                                modifier = Modifier.border(2.dp, Color.White.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White.copy(alpha = 0.5f)
                                )
                            ) { Text("⬇") }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick =   { isPaused = true },
                                modifier = Modifier.border(2.dp, Color.White.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White.copy(alpha = 0.5f)
                                )
                            ) { Text("II") }

                            Spacer(modifier = Modifier.height(16.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = {
                                            disparar(
                                                animatedX,
                                                animatedY,
                                                direccion,
                                                disparos,
                                                { balasRestantes },           // getter
                                                { nuevoValor -> balasRestantes = nuevoValor }, // setter
                                                { valor -> mostrarPistola = valor }
                                                )


                                        },
                                        modifier = Modifier.border(
                                            2.dp,
                                            Color.White.copy(alpha = 0.5f)
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = Color.White.copy(alpha = 0.5f)
                                        )
                                    ) { Text("A") }

                                    Button(
                                        onClick = {
                                            colocarObstaculo(posicionPersonaje, obstaculos, obstaculosRestantes)
                                        },
                                        modifier = Modifier.border(
                                            2.dp,
                                            Color.White.copy(alpha = 0.5f)
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = Color.White.copy(alpha = 0.5f)
                                        )
                                    ) { Text("D") }
                                }
                            }
                        }
                    }


                }
            }
        }


    }
}
@Composable
fun GameHeader(
    timeLeft: String,
    score: String,
    mission: String,
    vidas: Int
) {




    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeLeft,
            fontSize = 14.sp,
            color = Color.Yellow,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Score: $score",
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = mission,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.weight(2f)
        )

        // vidas (icono + texto)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.maricaabajo),
                contentDescription = "Icono vidas",
                modifier = Modifier.size(24.dp) // reducimos de 32x32 a 24x24
            )
            Text(
                text = " x $vidas",
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
