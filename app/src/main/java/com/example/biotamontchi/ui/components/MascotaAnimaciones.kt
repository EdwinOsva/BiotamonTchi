package com.example.biotamontchi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import com.example.biotamontchi.data.Etapa
import com.example.biotamontchi.data.MascotaBase
import com.example.biotamontchi.data.MascotaPlanta
import com.example.biotamontchi.data.RepositorioAnimaciones
import kotlinx.coroutines.delay


@Composable
fun DibujarAnimacionRiego(
    semillaPosicion: Offset,
    onAnimacionCompleta: () -> Unit
) {
    val imagenBoxSize = 100.dp
    val density = LocalDensity.current
    val imagenBoxSizePx = with(density) { imagenBoxSize.toPx() }
    val ajusteVertical = 40

    var cuadroActual by remember { mutableStateOf(1) }

    // Efecto que avanza los cuadros cada 120 ms
    LaunchedEffect(Unit) {
        while (cuadroActual < 8) {
            delay(180)
            cuadroActual++
        }
        delay(180)
        onAnimacionCompleta()
    }

    val imagenId = when (cuadroActual) {
        1 -> R.drawable.regar1
        2 -> R.drawable.regar2
        3 -> R.drawable.regar3
        4 -> R.drawable.regar4
        5 -> R.drawable.regar5
        6 -> R.drawable.regar6
        7 -> R.drawable.regar7
        8 -> R.drawable.regar8
        else -> R.drawable.regar1 // por seguridad, nunca debería usarse
    }

    Box(
        modifier = Modifier
            .size(imagenBoxSize)
            .offset {
                IntOffset(
                    x = (semillaPosicion.x - imagenBoxSizePx / 2).toInt(),
                    y = (semillaPosicion.y - imagenBoxSizePx / 2 - ajusteVertical).toInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Animación Riego",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DibujarAnimacionPlagas(
    semillaPosicion: Offset,
    nivelPlagas: Int, // 🔁 nuevo parámetro
    onAnimacionCompleta: () -> Unit
)
{
    val imagenBoxSize = 100.dp
    val density = LocalDensity.current
    val imagenBoxSizePx = with(density) { imagenBoxSize.toPx() }
    val ajusteVertical = 40

    var cuadroActual by remember { mutableStateOf(1) }

    // Avanzar cada 150ms en loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(150)
            cuadroActual = (cuadroActual % 4) + 1 // Ciclo de 1 a 4
        }
    }

    val imagenId = when (cuadroActual) {
        1 -> if (nivelPlagas > 6) R.drawable.insectos1 else R.drawable.insectos01
        2 -> if (nivelPlagas > 6) R.drawable.insectos2 else R.drawable.insectos02
        3 -> if (nivelPlagas > 6) R.drawable.insectos3 else R.drawable.insectos03
        4 -> if (nivelPlagas > 6) R.drawable.insectos4 else R.drawable.insectos04
        else -> R.drawable.insectos01
    }


    Box(
        modifier = Modifier
            .size(imagenBoxSize)
            .offset {
                IntOffset(
                    x = (semillaPosicion.x - imagenBoxSizePx / 2).toInt(),
                    y = (semillaPosicion.y - imagenBoxSizePx / 2 - ajusteVertical).toInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Animación Plagas",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun DibujarAnimacionBrotes(
    semillaPosicion: Offset,
    nivelNutrientes: Int, // 🔁 nuevo parámetro
    onAnimacionCompleta: () -> Unit
)
{
    val imagenBoxSize = 100.dp
    val density = LocalDensity.current
    val imagenBoxSizePx = with(density) { imagenBoxSize.toPx() }
    val ajusteVertical = 40

    var cuadroActual by remember { mutableStateOf(1) }
    // ⚡ Cambiar velocidad según nutrientes
    val velocidad = if (nivelNutrientes > 6) 150L else 220L

    // Avanzar cada 150ms en loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(velocidad)
            cuadroActual = (cuadroActual % 4) + 1 // Ciclo de 1 a 4
        }
    }

    val imagenId = when (cuadroActual) {
        1 ->   R.drawable.brotes1
        2 ->   R.drawable.brotes2
        3 ->  R.drawable.brotes3
        4 ->   R.drawable.brotes4
        else -> R.drawable.brotes1
    }


    Box(
        modifier = Modifier
            .size(imagenBoxSize)
            .offset {
                IntOffset(
                    x = (semillaPosicion.x - imagenBoxSizePx / 2).toInt(),
                    y = (semillaPosicion.y - imagenBoxSizePx / 2 - ajusteVertical).toInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Animación Plagas",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DibujarPersonajeAnimacionLoop(
    tipoGeneral: Int,
    etapa: Etapa,
    especie: String,
    estado: String = "normal",
    semillaPosicion: Offset
) {
    val imagenBoxSize = 100.dp
    val density = LocalDensity.current
    val imagenBoxSizePx = with(density) { imagenBoxSize.toPx() }
    val ajusteVertical = 50

    var cuadroActual by remember { mutableStateOf(0) }

    val imagenes = RepositorioAnimaciones.animaciones[tipoGeneral]
        ?.get(etapa)
        ?.get(estado)
        ?.get(especie.lowercase())
        ?: emptyList()

    if (imagenes.isEmpty()) return

    LaunchedEffect(imagenes) {
        cuadroActual = 0 // ← reinicia animación al cambiar etapa/especie/estado
        while (true) {
            delay(180)
            cuadroActual = (cuadroActual + 1) % imagenes.size
        }
    }

    val imagenId = imagenes[cuadroActual % imagenes.size]

    Box(
        modifier = Modifier
            .size(imagenBoxSize)
            .offset {
                IntOffset(
                    x = (semillaPosicion.x - imagenBoxSizePx / 2).toInt(),
                    y = (semillaPosicion.y - imagenBoxSizePx / 2 - ajusteVertical).toInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Animación personaje",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DibujarAnimacionSembrar(
    semillaPosicion: Offset
) {
    val imagenBoxSize = 100.dp
    val density = LocalDensity.current
    val imagenBoxSizePx = with(density) { imagenBoxSize.toPx() }
    val ajusteVertical = 50

    var cuadroActual by remember { mutableStateOf(1) }

    // Loop infinito de la animación
    LaunchedEffect(Unit) {
        while (true) {
            delay(180)
            cuadroActual = if (cuadroActual < 8) cuadroActual + 1 else 1
        }
    }

    val imagenId = when (cuadroActual) {
        1 -> R.drawable.sembrar1
        2 -> R.drawable.sembrar2
        3 -> R.drawable.sembrar3
        4 -> R.drawable.sembrar4
        5 -> R.drawable.sembrar5
        6 -> R.drawable.sembrar6
        7 -> R.drawable.sembrar5
        8 -> R.drawable.sembrar6
        else -> R.drawable.sembrar1 // seguridad
    }

    Box(
        modifier = Modifier
            .size(imagenBoxSize)
            .offset {
                IntOffset(
                    x = (semillaPosicion.x - imagenBoxSizePx / 2).toInt(),
                    y = (semillaPosicion.y - imagenBoxSizePx / 2 - ajusteVertical).toInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Animación Sembrar",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DibujarAnimacionPlagas2(
    mascota: MascotaBase,
    semillaPosicion: Offset,
    nivelPlagas: Int,
    onAnimacionCompleta: () -> Unit
) {
    val imagenBoxSize = 100.dp
    val density = LocalDensity.current
    val imagenBoxSizePx = with(density) { imagenBoxSize.toPx() }
    val ajusteVertical = 40

    var cuadroActual by remember { mutableStateOf(1) }

    // Animación cuadro a cuadro
    LaunchedEffect(Unit) {
        while (true) {
            delay(150)
            cuadroActual = (cuadroActual % 4) + 1
        }
    }

    val imagenes = (mascota as? MascotaPlanta)?.animacionesDePlaga(nivelPlagas)
        ?: mascota.animacionesDePlaga() // por si no es planta

    val imagenId = imagenes.getOrElse(cuadroActual - 1) { imagenes.first() }

    Box(
        modifier = Modifier
            .size(imagenBoxSize)
            .offset {
                IntOffset(
                    x = (semillaPosicion.x - imagenBoxSizePx / 2).toInt(),
                    y = (semillaPosicion.y - imagenBoxSizePx / 2 - ajusteVertical).toInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Animación Plagas",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

