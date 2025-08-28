package com.example.biotamontchi.model


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.biotamontchi.R
import kotlinx.coroutines.delay
import java.util.LinkedList
import java.util.Queue


data class Personaje(
    val posicionOriginal: Pair<Int, Int>,
    var posicion: Pair<Int, Int>,
    var destino: Pair<Int, Int>,
    var direccion: Int = 0,
    var enMovimiento: Boolean = false,
    var frameIndex: Int = 0, // Índice del sprite
    var tocado: Boolean = false,
    var enEsquina: Boolean = false,
    var enPersonaje: Boolean = false,
    var destruido: Boolean = false // Nuevo campo para saber si el enemigo está destruido
)
//val tileSize = 32

data class Disparo(
    var x: Float,
    var y: Float,
    val direccion: Int,
    var frame: Int = 0,
    var activo: Boolean = true,
    var enExplosion: Boolean = false,
    var explosionFrame: Int = 0
)

@Composable
fun CountdownTimer(
    onTimeUp: () -> Unit,
    onWarning: (() -> Unit)? = null, // 🔹 nuevo callback
    isPaused: Boolean,
    content: @Composable (String) -> Unit
) {
    var timeLeft by remember { mutableStateOf(180) }

    LaunchedEffect(Unit, isPaused) {
        while (timeLeft > 0) {
            delay(1000L)
            if (!isPaused) {
                timeLeft--

                // 🔹 Aviso cuando quedan 20 segundos
                if (timeLeft == 20) {
                    onWarning?.invoke()
                }
            }
        }
        if (timeLeft <= 0) {
            onTimeUp()
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    content(timeString)
}




// Coordenadas posibles para moverse
val movimientosPosibles = listOf(
    Pair(0, 1), // Abajo
    Pair(0, -1), // Arriba
    Pair(-1, 0), // Izquierda
    Pair(1, 0)   // Derecha
)
// Posiciones de espera para cada fantasma
val posicionesEspera = listOf(
    Pair(1, 1),  // Esquina superior izquierda
    Pair(20, 1),  // Esquina superior derecha
    Pair(1, 41),  // Esquina inferior izquierda
    Pair(20, 41)  // Esquina inferior derecha
)
// Paso 1: Asociar imágenes a números
val imageMap = mapOf(
    17 to R.drawable.l01,
    18 to R.drawable.l02,
    12 to R.drawable.l03,
    15 to R.drawable.l04,
    13 to R.drawable.l05,
    16 to R.drawable.l06,
    14 to R.drawable.l07,
    11 to R.drawable.l08,
    21 to R.drawable.l09,
    22 to R.drawable.l10,
    23 to R.drawable.l11,
    24 to R.drawable.l12,
    28 to R.drawable.l13,
    26 to R.drawable.l14,
    25 to R.drawable.l15,
    27 to R.drawable.l16,
    31 to R.drawable.l17,
    32 to R.drawable.l18,
    33 to R.drawable.l19,
    34 to R.drawable.l20
)

// Paso 2: Definir la matriz del mapa

val mapa: List<List<Int>> = listOf(

    listOf(11,12, 12, 12, 12, 12,12,  12, 12, 12,  12, 12,    12,  12,12,  12, 12, 12,12, 12, 12, 13  ),
    listOf(17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 33, 33, 33, 33, 33, 33, 28, 0, 0, 0, 18),
    listOf(17, 0, 33, 26, 0, 33, 33, 33, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 18),
    listOf(17, 0, 0, 32, 0, 0, 0, 0, 32, 0, 32, 0, 25, 26, 0, 33, 0, 33, 33, 28, 0, 18),
    listOf(17, 33, 0, 32, 0, 25, 26, 0, 32, 0, 32, 0, 32, 32, 0, 0, 0, 0, 0, 0, 0, 18),
    listOf(17, 0, 0, 0, 0, 27, 28, 0, 0, 0, 0, 0, 27, 28, 0, 33, 26, 0, 33, 33, 33, 18),
    listOf(17, 0, 25, 26, 0, 0, 0, 33, 33, 0, 32, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 18),
    listOf(17, 0, 27, 24, 33, 33, 0, 0, 0, 0, 27, 33, 33, 33, 26, 0, 21, 33, 0, 33, 0, 18),
    listOf(17, 0, 0, 0, 0, 0, 0, 25, 26, 0, 0, 0, 0, 0, 32, 0, 32, 0, 0, 0, 0, 18),
    listOf(17, 26, 0, 33, 33, 0, 25, 31, 22, 0, 32, 0, 32, 0, 0, 0, 0, 0, 25, 26, 0, 18),
    listOf(17, 28, 0, 0, 0, 0, 21, 24, 28, 0, 0, 0, 21, 23, 26, 0, 32, 0, 21, 22, 0, 18),
    listOf(17, 0, 0, 25, 23, 33, 28, 0, 0, 0, 32, 0, 21, 31, 28, 0, 32, 0, 21, 28, 0, 18),
    listOf(17, 0, 25, 31, 22, 0, 0, 0, 32, 0, 0, 0, 21, 22, 0, 0, 32, 0, 32, 0, 0, 18),
    listOf(17, 0, 27, 24, 28, 0, 25, 33, 28, 0, 32, 0, 27, 28, 0, 33, 22, 0, 32, 0, 33, 18),
    listOf(17, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 32, 0, 0, 18),
    listOf(17, 0, 32, 0, 33, 33, 28, 0, 33, 23, 26, 0, 25, 33, 33, 0, 0, 0, 0, 33, 0, 18),
    listOf(17, 0, 32, 0, 0, 0, 0, 0, 0, 21, 22, 0, 32, 0, 0, 0, 25, 26, 0, 0, 0, 18),
    listOf(17, 0, 32, 0, 32, 0, 25, 33, 0, 21, 22, 0, 32, 0, 32, 0, 27, 31, 26, 0, 33, 18),
    listOf(17, 0, 32, 0, 0, 0, 32, 0, 0, 21, 22, 0, 0, 0, 32, 0, 0, 21, 22, 0, 0, 18),
    listOf(17, 0, 27, 33, 26, 0, 32, 0, 33, 31, 24, 33, 33, 0, 21, 33, 0, 27, 31, 33, 0, 18),
    listOf(17, 0, 0, 0, 32, 0, 0, 0, 0, 32, 0, 0, 0, 0, 32, 0, 0, 0, 32, 0, 0, 18),
    listOf(17, 0, 33, 0, 32, 0, 33, 33, 0, 32, 0, 33, 33, 33, 28, 0, 32, 0, 32, 0, 25, 18),
    listOf(17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 27, 18),
    listOf(17, 0, 32, 0, 25, 33, 33, 26, 0, 32, 0, 25, 33, 33, 33, 0, 32, 0, 32, 0, 0, 18),
    listOf(17, 0, 32, 0, 32, 0, 0, 32, 0, 32, 0, 32, 0, 0, 0, 0, 0, 0, 21, 33, 0, 18),
    listOf(17, 0, 32, 0, 32, 0, 0, 32, 0, 32, 0, 32, 0, 33, 33, 33, 33, 0, 32, 0, 0, 18),
    listOf(17, 0, 32, 0, 32, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 25, 18),
    listOf(17, 0, 32, 0, 32, 0, 0, 32, 0, 32, 0, 33, 33, 33, 0, 33, 26, 0, 0, 0, 27, 18),
    listOf(17, 0, 0, 0, 32, 0, 0, 32, 0, 32, 0, 0, 0, 0, 0, 0, 27, 33, 26, 0, 0, 18),
    listOf(17, 0, 32, 0, 27, 33, 33, 28, 0, 27, 33, 33, 0, 25, 33, 0, 0, 0, 27, 33, 0, 18),
    listOf(17, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 32, 0, 0, 0, 0, 18),
    listOf(17, 0, 27, 33, 26, 0, 33, 33, 33, 33, 0, 33, 33, 28, 0, 25, 22, 0, 33, 33, 0, 18),
    listOf(17, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 28, 0, 0, 0, 0, 18),
    listOf(17, 23, 33, 0, 0, 0, 33, 26, 0, 25, 23, 26, 0, 32, 0, 0, 0, 0, 25, 33, 0, 18),
    listOf(17, 28, 0, 0, 32, 0, 0, 32, 0, 27, 31, 22, 0, 32, 0, 25, 26, 0, 32, 0, 0, 18),
    listOf(17, 0, 0, 25, 24, 33, 0, 32, 0, 0, 27, 28, 0, 32, 0, 27, 28, 0, 32, 0, 33, 18),
    listOf(17, 0, 33, 28, 0, 0, 0, 0, 33, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 18),
    listOf(17, 0, 0, 0, 0, 33, 33, 0, 0, 0, 25, 26, 0, 0, 0, 33, 33, 26, 0, 32, 0, 18),
    listOf(17, 0, 33, 33, 0, 0, 0, 0, 32, 0, 21, 22, 0, 32, 0, 0, 0, 32, 0, 32, 0, 18),
    listOf(17, 0, 0, 0, 33, 33, 33, 0, 32, 0, 27, 28, 0, 27, 33, 33, 0, 0, 0, 32, 0, 18),
    listOf(17, 0, 32, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 25, 23, 22, 0, 18),
    listOf(17, 0, 27, 33, 33, 0, 33, 33, 24, 33, 33, 33, 33, 0, 33, 33, 0, 27, 24, 28, 0, 18),
    listOf(17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18),
    listOf( 14,  15, 15, 15,  15, 15, 15, 15, 15, 15, 15, 15,  15, 15,  15, 15,15,15,  15,   15,  15,  16   ),

    )


// Lógica para mover al personaje con validaciones
fun moverPersonaje(
    mapa: List<List<Int>>,
    obstaculos: List<Pair<Int, Int>>,
    posicionActual: Pair<Int, Int>,
    dx: Int,
    dy: Int
): Pair<Int, Int> {
    val nuevaPosicion = Pair(posicionActual.first + dx, posicionActual.second + dy)

    // Verificamos si el nuevo espacio está vacío y no hay obstáculo
    return if (mapa[nuevaPosicion.second][nuevaPosicion.first] == 0 &&
        !obstaculos.contains(nuevaPosicion)
    ) {
        nuevaPosicion
    } else {
        posicionActual
    }
}

fun esValida(pos: Pair<Int, Int>, obstaculos: List<Pair<Int, Int>>): Boolean {
    val (x, y) = pos
    return mapa.getOrNull(y)?.getOrNull(x) == 0 && !obstaculos.contains(pos)  // 0 es camino transitable y no debe haber obstáculo
}


// Tu función disparar ahora valida balas
fun disparar(
    animatedX: Float,
    animatedY: Float,
    direccion: Int,
    disparos: MutableList<Disparo>,
    getBalas: () -> Int,
    setBalas: (Int) -> Unit,
    setMostrarPistola: (Boolean) -> Unit
) {
    if (getBalas() > 0) {
        disparos.add(Disparo(animatedX, animatedY, direccion))
        setBalas(getBalas() - 1)
    } else {

        setMostrarPistola(true) // avisamos a la UI que muestre la pistola
        // sin balas
    }
}


fun colocarObstaculo(
    posicionPersonaje: Pair<Int, Int>,
    obstaculos: MutableList<Pair<Int, Int>>,
    obstaculosRestantes: MutableState<Int>
) {
    if (obstaculosRestantes.value > 0 && !obstaculos.contains(posicionPersonaje)) {
        obstaculos.add(posicionPersonaje)
        obstaculosRestantes.value--
    }
}
/*
fun colocarObstaculo(
    posicionPersonaje: Pair<Int, Int>,
    obstaculos: MutableList<Pair<Int, Int>>
) {
    if (!obstaculos.contains(posicionPersonaje)) {
        obstaculos.add(posicionPersonaje)
    }
}*/



fun buscarCaminoBFS(inicio: Pair<Int, Int>, destino: Pair<Int, Int>,   obstaculos: List<Pair<Int, Int>>, mapa: List<List<Int>>): List<Pair<Int, Int>> {
    val cola = ArrayDeque<List<Pair<Int, Int>>>()
    val visitados = mutableSetOf<Pair<Int, Int>>()

    // Empezamos con el punto de inicio
    cola.add(listOf(inicio))
    visitados.add(inicio)

    while (cola.isNotEmpty()) {
        val camino = cola.removeFirst()
        val posicionActual = camino.last()

        // Si hemos llegado al destino, retornamos el camino encontrado
        if (posicionActual == destino) {
            return camino
        }

        // Intentamos movernos en todas las direcciones posibles
        for (movimiento in movimientosPosibles) {
            val nuevaPosicion = Pair(
                posicionActual.first + movimiento.first,
                posicionActual.second + movimiento.second
            )
            if (nuevaPosicion.first in 0 until mapa[0].size && nuevaPosicion.second in 0 until mapa.size &&
                mapa[nuevaPosicion.second][nuevaPosicion.first] == 0 && // Solo mover si es un espacio libre
                nuevaPosicion !in visitados &&
                !obstaculos.contains(nuevaPosicion) // Verificar que no sea un obstáculo
            ) {
                visitados.add(nuevaPosicion)
                cola.add(camino + nuevaPosicion)
            }
        }
    }
    return emptyList() // Si no hay camino, retornamos una lista vacía
}

fun moverFantasma(
    personajes: MutableList<Personaje>,
    index: Int,
    posicionPersonaje: Pair<Int, Int>,
    obstaculos: List<Pair<Int, Int>>,
    mapa: List<List<Int>>
) {
    val personaje = personajes[index]
    val camino = buscarCaminoBFS(
        inicio = personaje.posicion,
        destino = posicionPersonaje,
        obstaculos = obstaculos,
        mapa = mapa
    )

    if (camino.size > 1) {
        val (xActual, yActual) = personaje.posicion
        val (xNuevo, yNuevo) = camino[1]

        val nuevaDireccion = when {
            xNuevo > xActual -> 3
            xNuevo < xActual -> 2
            yNuevo > yActual -> 0
            yNuevo < yActual -> 1
            else -> personaje.direccion
        }

        personajes[index] = personajes[index].copy(
            destino = Pair(xNuevo, yNuevo),
            posicion = Pair(xNuevo, yNuevo),
            direccion = nuevaDireccion
        )
    }
}


fun buscarRuta(inicio: Pair<Int, Int>, destino: Pair<Int, Int>,  obstaculos: List<Pair<Int, Int>>,): Pair<Int, Int>? {
    val queue: Queue<Pair<Int, Int>> = LinkedList()
    val visitado = mutableSetOf<Pair<Int, Int>>()
    val padre = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()

    queue.add(inicio)
    visitado.add(inicio)

    // Direcciones posibles (Abajo, Arriba, Izquierda, Derecha)
    val direcciones = listOf(Pair(0, 1), Pair(0, -1), Pair(-1, 0), Pair(1, 0))

    while (queue.isNotEmpty()) {
        val actual = queue.poll()

        // Si llegamos al destino, reconstruimos el camino
        if (actual == destino) {
            var paso = destino
            while (padre[paso] != inicio) {
                paso = padre[paso] ?: break
            }
            return paso // Primer movimiento en la dirección correcta
        }

        // Explorar vecinos
        for ((dx, dy) in direcciones) {
            val vecino = Pair(actual.first + dx, actual.second + dy)

            // Si la casilla es válida, no tiene obstáculos y no ha sido visitada
            if (esValida(vecino, obstaculos) && vecino !in visitado) {
                queue.add(vecino)
                visitado.add(vecino)
                padre[vecino] = actual
            }
        }
    }
    return null // No hay camino posible
}


fun calcularDireccion(
    origen: Pair<Int, Int>,
    destino: Pair<Int, Int>,
    direccionActual: Int
): Int {
    val (ox, oy) = origen
    val (dx, dy) = destino

    return when {
        dx > ox -> 3  // Derecha
        dx < ox -> 2  // Izquierda
        dy > oy -> 0  // Abajo
        dy < oy -> 1  // Arriba
        else -> direccionActual  // Mantiene la dirección previa si no hay cambio
    }
}



fun moverBlinky(
    index: Int,
    personajes: MutableList<Personaje>,
    posicionPersonaje: Pair<Int, Int>,
    direccionActual: Int,
    obstaculos: List<Pair<Int, Int>>
) {
    val blinky = personajes[index]
    val destino = posicionPersonaje // Pac-Man es el objetivo directo

    if (blinky.tocado) {
        // Si el fantasma tocó a Pac-Man, moverse hacia la esquina
        val nuevaPosicionEsquina = posicionesEspera[index % posicionesEspera.size] // Selecciona una esquina
        val nuevaPosicion = buscarRuta(blinky.posicion, nuevaPosicionEsquina, obstaculos)

        if (nuevaPosicion != null) {
            val nuevaDireccion = calcularDireccion(blinky.posicion, nuevaPosicion, direccionActual)
            personajes[index] = blinky.copy(
                posicion = nuevaPosicion,
                destino = nuevaPosicion,
                direccion = nuevaDireccion
            )

            // Si el fantasma llega a la esquina, vuelve a buscar a Pac-Man
            if (nuevaPosicion == nuevaPosicionEsquina) {
                personajes[index] = blinky.copy(tocado = false)
            }
        }
    } else {
        // Si el fantasma no ha tocado a Pac-Man, continuar moviéndolo hacia Pac-Man
        if (blinky.posicion == destino) {
            // Al tocar a Pac-Man, cambiar tocado a true y empezar a ir hacia la esquina
            personajes[index] = blinky.copy(tocado = true)
        } else {
            // Continuar moviéndose hacia Pac-Man
            val nuevaPosicion = buscarRuta(blinky.posicion, destino, obstaculos)
            if (nuevaPosicion != null) {
                val nuevaDireccion = calcularDireccion(blinky.posicion, nuevaPosicion, direccionActual)
                personajes[index] = blinky.copy(
                    posicion = nuevaPosicion,
                    destino = nuevaPosicion,
                    direccion = nuevaDireccion
                )
            }
        }
    }
}




fun moverPinky(
    index: Int,
    personajes: MutableList<Personaje>,
    posicionPersonaje: Pair<Int, Int>,
    direccionPersonaje: Int,
    obstaculos: List<Pair<Int, Int>>
) {
    val pinky = personajes[index]
    val (px, py) = posicionPersonaje

    // Determinar la posición de anticipación de Pinky basada en la dirección de Pac-Man
    var anticipacion = when (direccionPersonaje) {
        0 -> Pair(px, py + 4)   // Abajo
        1 -> Pair(px, py - 4)   // Arriba
        2 -> Pair(px - 4, py)   // Izquierda
        3 -> Pair(px + 4, py)   // Derecha
        else -> Pair(px, py)
    }

    if (!esValida(anticipacion, obstaculos)) {
        anticipacion = Pair(px, py) // Si no es válida, mantener Pac-Man como referencia
    }

    if (pinky.tocado) {
        // Si Pinky tocó a Pac-Man, moverse hacia su esquina
        val nuevaPosicionEsquina = posicionesEspera[index % posicionesEspera.size]
        val nuevaPosicion = buscarRuta(pinky.posicion, nuevaPosicionEsquina, obstaculos)

        if (nuevaPosicion != null) {
            personajes[index] = pinky.copy(
                posicion = nuevaPosicion,
                destino = nuevaPosicion,
                direccion = calcularDireccion(pinky.posicion, nuevaPosicion, direccionPersonaje)
            )

            // Si llegó a la esquina, vuelve a buscar a Pac-Man
            if (nuevaPosicion == nuevaPosicionEsquina) {
                personajes[index] = pinky.copy(tocado = false)
            }
        }
    } else {
        // Si Pinky aún no ha tocado a Pac-Man, seguirlo con anticipación
        if (pinky.posicion == anticipacion) {
            // Si llega a la posición anticipada (o Pac-Man), cambiar a estado tocado
            personajes[index] = pinky.copy(tocado = true)
        } else {
            val nuevaPosicion = buscarRuta(pinky.posicion, anticipacion, obstaculos)
            if (nuevaPosicion != null) {
                personajes[index] = pinky.copy(
                    posicion = nuevaPosicion,
                    destino = nuevaPosicion,
                    direccion = calcularDireccion(pinky.posicion, nuevaPosicion, direccionPersonaje)
                )
            }
        }
    }
}

fun moverInky(
    index: Int,
    personajes: MutableList<Personaje>,
    posicionPersonaje: Pair<Int, Int>,
    direccionActual: Int,
    direccion: Int,
    obstaculos: List<Pair<Int, Int>>
) {
    val inky = personajes[index]
    val (px, py) = posicionPersonaje
    val direccionPersonaje = direccion

    // Invertir la anticipación: en lugar de adelantarse, se mueve hacia atrás
    var anticipacion = when (direccionPersonaje) {
        0 -> Pair(px, py - 2)   // Pac-Man va abajo → Inky va arriba
        1 -> Pair(px, py + 2)   // Pac-Man va arriba → Inky va abajo
        2 -> Pair(px + 2, py)   // Pac-Man va izquierda → Inky va derecha
        3 -> Pair(px - 2, py)   // Pac-Man va derecha → Inky va izquierda
        else -> Pair(px, py)
    }

    if (!esValida(anticipacion, obstaculos)) {
        anticipacion = Pair(px, py) // Si no es válida, mantener Pac-Man como referencia
    }

    if (inky.tocado) {
        // Si Inky tocó a Pac-Man, moverse a su esquina
        val nuevaPosicionEsquina = posicionesEspera[index % posicionesEspera.size]
        val nuevaPosicion = buscarRuta(inky.posicion, nuevaPosicionEsquina, obstaculos)

        if (nuevaPosicion != null) {
            val nuevaDireccion = calcularDireccion(inky.posicion, nuevaPosicion, direccionActual)
            personajes[index] = inky.copy(
                posicion = nuevaPosicion,
                destino = nuevaPosicion,
                direccion = nuevaDireccion
            )

            // Si llegó a la esquina, vuelve a buscar a Pac-Man
            if (nuevaPosicion == nuevaPosicionEsquina) {
                personajes[index] = inky.copy(tocado = false)
            }
        }
    } else {
        // Si Inky aún no ha tocado a Pac-Man, seguirlo con la nueva anticipación
        if (inky.posicion == anticipacion) {
            // Si llega a la posición anticipada (o Pac-Man), cambiar a estado tocado
            personajes[index] = inky.copy(tocado = true)
        } else {
            val nuevaPosicion = buscarRuta(inky.posicion, anticipacion, obstaculos)
            if (nuevaPosicion != null) {
                val nuevaDireccion = calcularDireccion(inky.posicion, nuevaPosicion, direccionActual)
                personajes[index] = inky.copy(
                    posicion = nuevaPosicion,
                    destino = nuevaPosicion,
                    direccion = nuevaDireccion
                )
            }
        }
    }
}



fun moverClyde(
    index: Int,
    personajes: MutableList<Personaje>,
    posicionPersonaje: Pair<Int, Int>,
    direccionActual: Int,
    obstaculos: List<Pair<Int, Int>>
) {
    val clyde = personajes[index]
    val destino = posicionPersonaje // Pac-Man es el objetivo
    val distancia =
        Math.abs(destino.first - clyde.posicion.first) + Math.abs(destino.second - clyde.posicion.second)
    val nuevaPosicionEsquina =
        posicionesEspera[index % posicionesEspera.size] // Determina la esquina asignada

    if (clyde.enPersonaje) {
        // Si enPersonaje está activado, Clyde debe moverse a su esquina SÍ O SÍ
        val nuevaPosicion = buscarRuta(clyde.posicion, nuevaPosicionEsquina, obstaculos)
        if (nuevaPosicion != null) {
            val nuevaDireccion = calcularDireccion(clyde.posicion, nuevaPosicion, direccionActual)
            personajes[index] = clyde.copy(
                posicion = nuevaPosicion,
                destino = nuevaPosicion,
                direccion = nuevaDireccion
            )
        }

        // Si Clyde llega a la esquina, desactiva enPersonaje y cambia a Blinky
        if (nuevaPosicion == nuevaPosicionEsquina) {
            personajes[index] = clyde.copy(enPersonaje = false, enEsquina = true)
        }
    } else if (clyde.enEsquina) {
        // Si Clyde está en la esquina, se comporta como Blinky hasta que la distancia sea ≤ 15
        moverBlinky(index, personajes, posicionPersonaje, direccionActual, obstaculos)

        // Cuando la distancia es menor o igual a 15, vuelve a comportarse como Clyde
        if (distancia <= 15) {
            personajes[index] = clyde.copy(enEsquina = false)
        }
    } else {
        if (distancia > 15) {
            // Si la distancia a Pac-Man es mayor a 15, Clyde va hacia su esquina
            val nuevaPosicion = buscarRuta(clyde.posicion, nuevaPosicionEsquina, obstaculos)
            if (nuevaPosicion != null) {
                val nuevaDireccion = calcularDireccion(clyde.posicion, nuevaPosicion, direccionActual)
                personajes[index] = clyde.copy(
                    posicion = nuevaPosicion,
                    destino = nuevaPosicion,
                    direccion = nuevaDireccion
                )
            }

            // Si Clyde llega a la esquina, cambia al comportamiento de Blinky
            if (nuevaPosicion == nuevaPosicionEsquina) {
                personajes[index] = clyde.copy(enEsquina = true)
            }
        } else {
            // Si Clyde toca a Pac-Man, activar enPersonaje para ir a la esquina
            if (clyde.posicion == destino) {
                personajes[index] = clyde.copy(enPersonaje = true)
            } else {
                // Si la distancia es menor o igual a 15, Clyde persigue a Pac-Man
                val nuevaPosicion = buscarRuta(clyde.posicion, destino, obstaculos)
                if (nuevaPosicion != null) {
                    val nuevaDireccion = calcularDireccion(clyde.posicion, nuevaPosicion, direccionActual)
                    personajes[index] = clyde.copy(
                        posicion = nuevaPosicion,
                        destino = nuevaPosicion,
                        direccion = nuevaDireccion
                    )
                }
            }
        }
    }
}



