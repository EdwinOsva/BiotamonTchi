package com.example.biotamontchi.data


import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import kotlin.random.Random

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin


data class ObjetoPremio(
    val id: Int,
    val costo: Int,
    val tamano: Dp = 100.dp,
    val nombre: String,
 val drawable: Int,
    val sonido: Int ,
    val tipo: TipoPremio// <-- nuevo

)
enum class TipoPremio {
    BATE, PELOTA, SAXOFON, TORTUGA, HIERBA, FRUTA, PAPALOTE
}
data class PremioEnPantalla(
    val premio: ObjetoPremio,
    var posicion: Offset
)
val animacionesPremios = mapOf(
    0 to listOf(
        R.drawable.premio1,
        R.drawable.premio1_1,
        R.drawable.premio1_2,
        R.drawable.premio1_3,
        R.drawable.premio1_4,
        R.drawable.premio1_1,
        R.drawable.premio1_2,
        R.drawable.premio1_3,
        R.drawable.premio1_4,
        R.drawable.premio1_1,
        R.drawable.premio1_2,
        R.drawable.premio1_3,
        R.drawable.premio1_4
    ),
    1 to listOf(
        R.drawable.premio2,
        R.drawable.premio2_1,
        R.drawable.premio2_2,
        R.drawable.premio2_3,
        R.drawable.premio2_4,
        R.drawable.premio2_1,
        R.drawable.premio2_2,
        R.drawable.premio2_3,
        R.drawable.premio2_4,
        R.drawable.premio2_1,
        R.drawable.premio2_2,
        R.drawable.premio2_3,
        R.drawable.premio2_4,
        R.drawable.premio2_1,
        R.drawable.premio2_2,
        R.drawable.premio2_3,
        R.drawable.premio2_4,
    ),
    2 to listOf(
        R.drawable.premio3,
        R.drawable.premio3_0,
        R.drawable.premio3_1,
        R.drawable.premio3_2,
        R.drawable.premio3_4,
        R.drawable.premio3_0,
        R.drawable.premio3_1,
        R.drawable.premio3_2,
        R.drawable.premio3_3,
        R.drawable.premio3_3,
        R.drawable.premio3_4,
    ),
    3 to listOf(
        R.drawable.premio4,
        R.drawable.premio4_0,
        R.drawable.premio4_1,
        R.drawable.premio4_2,
        R.drawable.premio4_3,
        R.drawable.premio4_4,
        R.drawable.premio4_1,
        R.drawable.premio4_2,
        R.drawable.premio4_3,
        R.drawable.premio4_4,
        R.drawable.premio4_1,
        R.drawable.premio4_2,
        R.drawable.premio4_3,
        R.drawable.premio4_4
    ),
    4 to listOf(
        R.drawable.premio5,
        R.drawable.premio5_1,
        R.drawable.premio5_1,
        R.drawable.premio5_1,
        R.drawable.premio5_2,
        R.drawable.premio5_2,
        R.drawable.premio5_2,
        R.drawable.premio5_3,
        R.drawable.premio5_3,
        R.drawable.premio5_3,
        R.drawable.premio5_4,
        R.drawable.premio5_4,
        R.drawable.premio5_4,
        R.drawable.premio5_4,
        R.drawable.premio5_5,
        R.drawable.premio5_5,
        R.drawable.premio5_5,
        R.drawable.premio5_3,
        R.drawable.premio5_4,
        R.drawable.premio5_6,
        R.drawable.premio5_5,
        R.drawable.premio5_6
    ),
    5 to listOf(
        R.drawable.premio6_1,
        R.drawable.premio6_1,
        R.drawable.premio6_1,
        R.drawable.premio6_2,
        R.drawable.premio6_2,
        R.drawable.premio6_2,
        R.drawable.premio6_3,
        R.drawable.premio6_3,
        R.drawable.premio6_3,
        R.drawable.premio6_4,
        R.drawable.premio6_4,
        R.drawable.premio6_4,
        R.drawable.premio6_5,
        R.drawable.premio6_5,
        R.drawable.premio6_5,
        R.drawable.premio6_3,
        R.drawable.premio6_3,
        R.drawable.premio6_4,
        R.drawable.premio6_4,
        R.drawable.premio6_4,
        R.drawable.premio6_5,
        R.drawable.premio6_5,
        R.drawable.premio6_6,
        R.drawable.premio6_6,
        R.drawable.premio6_6
    )
)




fun premiar(
    premio: ObjetoPremio,
    premiosEnPantalla: MutableList<PremioEnPantalla>,
    prefs: PrefsManager,
    monedas: MutableState<Int>,
    screenWidthDp: Int,
    screenHeightDp: Int
) {
    val yaDesbloqueado = prefs.premioEstaDesbloqueado(premio.id)

    // Si no está desbloqueado, cobra el costo
    if (!yaDesbloqueado) {
        if (monedas.value < premio.costo) return // No alcanza
        monedas.value -= premio.costo
        prefs.guardarMonedas(monedas.value)
        prefs.guardarPremioDesbloqueado(premio.id)
    }

    // Generar posición aleatoria
    val x = Random.nextInt(100, screenWidthDp - 100).toFloat()
    val y = (screenHeightDp * 0.4f + Random.nextInt(0, 600)).toFloat()

    premiosEnPantalla.add(
        PremioEnPantalla(
            premio = premio,
            posicion = Offset(x, y)
        )
    )
}



fun detectaColisionConRegresar(
    premioPos: Offset,
    botonPos: Offset,
    tolerancia: Float = 80f
): Boolean {
    val distancia = (premioPos - botonPos).getDistance()
    return distancia < tolerancia
}

@Composable
fun DibujarAnimacionPremio(
    premio: ObjetoPremio,
    posicion: Offset,
    repeticiones: Int = 1,
    onAnimacionCompleta: () -> Unit // ← ✅ Esto es lo que faltaba
) {
    val imagenBoxSize = premio.tamano
    val density = LocalDensity.current
    val imagenBoxSizePx = with(density) { imagenBoxSize.toPx() }
    var cuadroActual by remember { mutableStateOf(0) }
    val listaFrames = animacionesPremios[premio.id] ?: emptyList()

    LaunchedEffect(premio.id) {
        repeat(repeticiones) {
            for (i in 0 until listaFrames.size) {
                cuadroActual = i
                delay(180)
            }
        }
        cuadroActual = -1
        onAnimacionCompleta() // ← ✅ Ya no dará error
    }

    val imagenId = listaFrames.getOrNull(cuadroActual)?.takeIf { cuadroActual >= 0 } ?: premio.drawable

    Box(
        modifier = Modifier
            .size(imagenBoxSize)
            .offset {
                IntOffset(
                    posicion.x.toInt(),
                    posicion.y.toInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Animación del Premio",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}


fun generarTrayectoriaSinRebotes(
    origen: Offset,
    velocidadInicial: Float,
    anguloInicial: Float,
    widthPx: Float,
    heightPx: Float,
    gravedad: Float = 9.8f,
    duracionTotal: Float = 2f,
    pasos: Int = 100
): List<Offset> {
    val puntos = mutableListOf<Offset>()

    val velocidadX = velocidadInicial * cos(Math.toRadians(anguloInicial.toDouble())).toFloat()
    val velocidadY = -velocidadInicial * sin(Math.toRadians(anguloInicial.toDouble())).toFloat()

    for (i in 0..pasos) {
        val t = i * (duracionTotal / pasos)
        val x = origen.x + velocidadX * t
        val y = origen.y + velocidadY * t + 0.5f * gravedad * t * t

        // Si se sale por los lados o por debajo, terminamos
        if (x < 0 || x > widthPx || y > heightPx) {
            break
        }

        puntos.add(Offset(x, y))
    }

    return puntos
}


@Composable
fun AnimarPelotaRebotando(
    imagenId: Int,
    puntos: List<Offset>,
    tamano: Dp,
    onFin: () -> Unit = {}
) {
    var index by remember { mutableStateOf(0) }

    LaunchedEffect(puntos) {
        for (i in puntos.indices) {
            index = i
            delay(16L) // ~60fps
        }
        onFin()
    }

    val posicion = puntos.getOrNull(index) ?: puntos.lastOrNull() ?: Offset.Zero

    Box(
        modifier = Modifier
            .size(tamano)
            .offset {
                IntOffset(
                    posicion.x.toInt(),
                    posicion.y.toInt()
                )
            }
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Pelota",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
fun detectaColisionEntrePremios(
    pos1: Offset,
    size1: DpSize,
    pos2: Offset,
    size2: DpSize
): Boolean {
    val rect1 = Rect(pos1, Size(size1.width.value, size1.height.value))
    val rect2 = Rect(pos2, Size(size2.width.value, size2.height.value))
    return rect1.overlaps(rect2)
}


fun generarTrayectoriaPapalote(
    origen: Offset,
    widthPx: Float,
    heightPx: Float,
    pasos: Int = 100
): List<Offset> {
    val puntos = mutableListOf<Offset>()
    val amplitudX = 80f // cuánto zigzaguea horizontalmente
    val avanceY = -heightPx / pasos // se va hacia arriba
    val avanceX = (widthPx * 0.5f) / pasos // avanza hacia la derecha

    for (i in 0 until pasos) {
        val offsetX = sin(i * 0.3f) * amplitudX
        val nuevoPunto = Offset(
            x = origen.x + i * avanceX + offsetX,
            y = origen.y + i * avanceY
        )
        puntos.add(nuevoPunto)
    }
    return puntos
}
@Composable
fun AnimarPapaloteZigZag(
    imagenId: Int,
    puntos: List<Offset>,
    tamano: Dp,
    onFin: () -> Unit
) {
    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        puntos.forEachIndexed { index, _ ->
            frameIndex = index
            delay(20L)
        }
        onFin()
    }

    if (frameIndex in puntos.indices) {
        val punto = puntos[frameIndex]
        Box(
            modifier = Modifier
                .offset { IntOffset(punto.x.toInt(), punto.y.toInt()) }
                .size(tamano)
        ) {
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = "Papalote",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
fun AnimarSaxofonFantastico(
    premio: PremioEnPantalla,
    onFin: (() -> Unit)? = null // opcional si quieres hacer algo al final
) {
    val tamano = premio.premio.tamano
    val saxofonDrawable = premio.premio.drawable

    val flip = remember { Animatable(1f) }
    val rotacion = remember { Animatable(0f) }
    val colorAnimado = remember { Animatable(Color.White) }

    // Animaciones simultáneas
    LaunchedEffect(Unit) {
        launch {
            rotacion.animateTo(
                720f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
        launch {
            flip.animateTo(
                -1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
        launch {
            val colores = listOf(Color.Cyan, Color.Yellow, Color.Magenta, Color.White)
            colores.forEach {
                colorAnimado.animateTo(it, tween(250))
                delay(200)
            }
        }

        delay(1200) // tiempo total antes de terminar
        onFin?.invoke()
    }

    // Render de la imagen animada
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    premio.posicion.x.toInt(),
                    premio.posicion.y.toInt()
                )
            }
            .size(tamano)
            .graphicsLayer {
                rotationZ = rotacion.value
                scaleX = flip.value
            }
    ) {
        Image(
            painter = painterResource(id = saxofonDrawable),
            contentDescription = "Saxofón mágico",
            colorFilter = ColorFilter.tint(colorAnimado.value),
            modifier = Modifier.fillMaxSize()
        )
    }
}
