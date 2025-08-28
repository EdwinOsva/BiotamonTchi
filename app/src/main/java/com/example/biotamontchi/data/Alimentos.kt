package com.example.biotamontchi.data



import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import kotlin.random.Random

data class FrutaAlimento(
    val id: Int,
    val costo: Int,
    val valorNutriente: Int,
    val valorFelicidad: Int,
    val tamano: Dp = 50.dp,
    val resistencia: Int = 0,
    val germinacion: Int = 0,
    val acuatica: Int = 0,
    val aerea: Int = 0,
    val parasita: Int = 0,
    val propagacion: Int = 0,
    val simbiosis: Int = 0,
    val adaptacion: Int = 0
)

data class FrutaEnPantalla(
    val fruta: FrutaAlimento,
    var posicion: Offset
)

val frutasDisponibles = listOf(
    FrutaAlimento(R.drawable.comi1, 5, 1, 1, 60.dp, 1, 0,0,1,0,1,0,0),//0
    FrutaAlimento(R.drawable.comi2, 6, 1, 1,  60.dp, 0, 1,1,1,0,1,0,0),//1
    FrutaAlimento(R.drawable.comi6, 6, 2, 1,  60.dp, 0, 1,1,1,0,1,0,0),//2
    FrutaAlimento(R.drawable.comi11, 7, 2, 1,  60.dp, 1, 2,0,0,0,1,0,1),//3
    FrutaAlimento(R.drawable.comi13, 9, 2, 1, 120.dp, 1, 1,1,0,1,1,1,0),//4
    FrutaAlimento(R.drawable.comi4, 10, 3, 2,  70.dp, 1, 0,0,1,2,0,1,1),//5
    FrutaAlimento(R.drawable.comi5, 12, 3, 2,  60.dp, 1, 1,0,1,0,1,0,2),//6
    FrutaAlimento(R.drawable.comi7, 13, 3, 2,  80.dp, 0,1,2,2,0,0,0,1),//7
    FrutaAlimento(R.drawable.comi3, 14, 3, 2,  40.dp, 1, 1,1,0,2,1,1,0),//8
    FrutaAlimento(R.drawable.comi8, 14, 3, 2,  60.dp, 0, 0,1,0,1,1,3,1),//9
    FrutaAlimento(R.drawable.comi12, 15, 3, 3,  60.dp, 1, 1,0,1,0,1,1,2),//10
    FrutaAlimento(R.drawable.comi9, 15, 4, 3,  110.dp, 2, 0,2,0,2,0,2,1),//11
    FrutaAlimento(R.drawable.comi10, 17, 4, 3,  60.dp, 1, 1,1,2 ,2,1,1,1)//12
)

fun alimentar(
    fruta: FrutaAlimento,
    frutasEnPantalla: MutableList<FrutaEnPantalla>,
    prefs: PrefsManager,
    monedas: MutableState<Int>,
    screenWidthDp: Int,
    screenHeightDp: Int
) {
    if (monedas.value >= fruta.costo) {
        monedas.value -= fruta.costo
        prefs.guardarMonedas(monedas.value)

        // Posición aleatoria
        val x = Random.nextInt(100, screenWidthDp - 50).toFloat()
        val y = (screenHeightDp * 0.3f + Random.nextInt(0, 1100)).toFloat()

        frutasEnPantalla.add(
            FrutaEnPantalla(
                fruta = fruta,
                posicion = Offset(x, y)
            )
        )

        // 👇 Guardar nuevas estadísticas
        fun actualizarEstadistica(nombre: String, valor: Int) {
            val actual = prefs.obtenerInt(nombre)
            prefs.guardarIndicador(nombre, actual + valor)

        }

        actualizarEstadistica("resistencia", fruta.resistencia)
        actualizarEstadistica("germinacion", fruta.germinacion)
        actualizarEstadistica("acuatica", fruta.acuatica)
        actualizarEstadistica("aerea", fruta.aerea)
        actualizarEstadistica("parasita", fruta.parasita)
        actualizarEstadistica("propagacion", fruta.propagacion)
        actualizarEstadistica("simbiosis", fruta.simbiosis)
        actualizarEstadistica("adaptacion", fruta.adaptacion)

    }
}



fun detectaColisionConPersonaje(frutaPos: Offset, personajePos: Offset, tolerancia: Float = 80f): Boolean {
    val distancia = (frutaPos - personajePos).getDistance()
    return distancia < tolerancia
}

