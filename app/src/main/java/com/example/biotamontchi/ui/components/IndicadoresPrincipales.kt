package com.example.biotamontchi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import com.example.biotamontchi.data.PrefsManager

//agua
fun calcularNivelAgua(prefs: PrefsManager, intervalo: Long = 1 * 60 * 1000): Int {
    val fechaUltimoRiego = prefs.obtenerLong("fechaUltimoRiego")
    val ahora = System.currentTimeMillis()
    val transcurrido = ahora - fechaUltimoRiego
    val porcentaje = transcurrido.toFloat() / intervalo.toFloat()
//pantalla apagada?
    return when {
        porcentaje >= 1f -> 0
        porcentaje < 0f -> 10
        else -> 10 - (porcentaje * 10).toInt()
    }

}

//feliz

fun calcularFelicidadDesdeUltimoValor(
    valorGuardado: Int,
    fechaUltima: Long,
    ahora: Long = System.currentTimeMillis(),
    intervalo: Long = 1000*60*5 // 5 minutos por punto con la app cerrada
): Int {
    val transcurrido = ahora - fechaUltima
    val intervalosPasados = (transcurrido / intervalo).toInt()

    return (valorGuardado - intervalosPasados).coerceIn(0, 10)
}
//plagas
fun calcularNivelDesdeUltimoValor2(
    valorGuardado: Int,
    fechaUltima: Long,
    ahora: Long = System.currentTimeMillis(),
    intervalo: Long
): Int {
    val transcurrido = ahora - fechaUltima
    val intervalosPasados = (transcurrido / intervalo).toInt()
    return (valorGuardado + intervalosPasados).coerceIn(0, 10)
}
//nutrientes, tieene lo mismo que felicidad pero otro intervalo d tiempo
fun calcularNivelDesdeUltimoValor(
    valorGuardado: Int,
    fechaUltima: Long,
    ahora: Long = System.currentTimeMillis(),
    intervalo: Long
): Int {
    val transcurrido = ahora - fechaUltima
    val intervalosPasados = (transcurrido / intervalo).toInt()
    return (valorGuardado - intervalosPasados).coerceIn(0, 10)
}

fun obtenerIconoIdIndicadores(valores: Int): Int {
    return when (valores) {
        10 -> R.drawable.indvertical10
        9 -> R.drawable.indvertical9
        8 -> R.drawable.indvertical8
        7 -> R.drawable.indvertical7
        6 -> R.drawable.indvertical6
        5 -> R.drawable.indvertical5
        4 -> R.drawable.indvertical4
        3 -> R.drawable.indvertical3
        2 -> R.drawable.indvertical2
        1 -> R.drawable.indvertical1
        else -> R.drawable.indvertical0
    }
}


@Composable
fun IndicadoresSuperpuestos(
    feliz: Int,
    nutrientes: Int,
    plagas: Int,
    modifier: Modifier = Modifier
) {
    // Dentro de tu Composable

    val iconoFeliz = if (feliz < 4) R.drawable.iconfeliz2 else R.drawable.iconfeliz1
    val iconoNutrientes = if (nutrientes < 4) R.drawable.iconnutrientes2 else R.drawable.iconnutrientes1
    val iconoPlagas = if (plagas > 4) R.drawable.iconplagas2 else R.drawable.iconplagas1


    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // NUTRIENTES
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 0.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = obtenerIconoIdIndicadores(nutrientes)),
                    contentDescription = "Nutrientes",
                    modifier = Modifier.size(80.dp)
                )
                Image(
                    painter = painterResource(id = iconoNutrientes),
                    contentDescription = "Icono abono",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // FELICIDAD (superpuesta)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp) // Aumenta o reduce para ajustar visualmente
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = iconoFeliz),
                    contentDescription = "Feliz arriba",
                    modifier = Modifier.size(40.dp)
                )
                Image(
                    painter = painterResource(id = obtenerIconoIdIndicadores(feliz)),
                    contentDescription = "Felicidad",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // PLAGAS (superpuesta aún más)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 40.dp) // Mueve más a la derecha
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = obtenerIconoIdIndicadores(plagas)),
                    contentDescription = "Plagas",
                    modifier = Modifier.size(80.dp)
                )
                Image(
                    painter = painterResource(id = iconoPlagas),
                    contentDescription = "Plaguicida",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
