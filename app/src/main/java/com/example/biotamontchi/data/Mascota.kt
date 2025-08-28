package com.example.biotamontchi.data


import androidx.compose.ui.geometry.Offset
import com.example.biotamontchi.R


data class Mascota(
    var agua: Int = 0,
    var feliz: Int = 0,
    var fechaUltimaFelicidad: Long = 0L,
    var nutrientes: Int = 0,
    var fechaUltimosNutrientes: Long = 0L,
    var plagas: Int = 0,
    var fechaUltimasPlagas: Long = 0L,
    var riegos: Int = 0,
    var fechaInicioJuego: Long = 0L,
    var fechaUltimoRiego: Long = 0L,
    var tiempoVida: Long = 0L,
    var ciclosCompletados: Int = 0,
    var indiceAnimacion: Int = 0,
    var resistencia: Int = 0,
    var germinacion: Int = 0,
    var acuatica: Int = 0,
    var aerea: Int = 0,
    var parasita: Int = 0,
    var propagacion: Int = 0,
    var simbiosis: Int = 0,
    var adaptacion: Int = 0,
    var etapa: Etapa = Etapa.SEMBRAR,
    var posicion: Offset? = null,
    var etapaMaxima: Etapa = Etapa.SEMBRAR,
    var tipoBiotamon: Int, // 1 = planta, 2 = animal, etc.
    var especie: String, // por ejemplo: "margarita", "lili", etc.
    var estado:String = "normal",
    var puntos:Int =0,

    // 👇 nuevo
    var causaMuerte: CausaMuerte = CausaMuerte.NINGUNA
)
enum class CausaMuerte {
    NINGUNA,
    DESCUIDO_SEQUIA,
    DESCUIDO_EXCESO_AGUA,
    DESCUIDO_NUTRIENTES,
    DESCUIDO_PLAGAS,
    VIEJA
}
class MascotaPlanta(
    override val datos: Mascota
) : MascotaBase(datos) {

    override val tipo: String
        get() = "planta"

    override val umbrales = listOf(
        15 * 1000L,
        1 * 60 * 60 * 1000L,
        4 * 60 * 60 * 1000L,
        12 * 60 * 60 * 1000L,
        22 * 60 * 60 * 1000L,
        23 * 60 * 60 * 1000L
    )

    override fun determinarEtapa(etapaActual: Etapa): Etapa {
        val tiempo = System.currentTimeMillis() - datos.fechaInicioJuego

        return when {
            tiempo < umbrales[0] -> if (etapaActual <= Etapa.SEMBRAR) Etapa.SEMBRAR else Etapa.SEMILLA
            tiempo < umbrales[1] -> Etapa.SEMILLA
            tiempo < umbrales[2] -> Etapa.PLANTULA
            tiempo < umbrales[3] -> Etapa.PLANTA
            tiempo < umbrales[4] -> Etapa.MADURA
            tiempo < umbrales[5] -> Etapa.MARCHITA
            else -> Etapa.MUERTA
        }
    }

    override fun animacionesDePlaga(nivelPlagas: Int): List<Int> {
        return if (nivelPlagas > 7 ) {
            listOf(R.drawable.insectos1, R.drawable.insectos2, R.drawable.insectos3, R.drawable.insectos4)
        } else {
            listOf(R.drawable.insectos01, R.drawable.insectos02, R.drawable.insectos03, R.drawable.insectos04)
        }
    }

    override fun animacionesDeBrotes(): List<Int> {
        return listOf(R.drawable.brotes1, R.drawable.brotes2, R.drawable.brotes3, R.drawable.brotes4)
    }

    override fun velocidadBrotes(nutrientes: Int): Long {
        return if (nutrientes > 6) 100L else 150L
    }

    override fun sonidoCuidado(): Int {
        return R.raw.clic1
    }
}



class MascotaAnimal(
    override val datos: Mascota
) : MascotaBase(datos) {

    override val tipo: String
        get() = "animal"

    override val umbrales = listOf(
        15 * 1000L,
        1 * 60 * 60 * 1000L,
        4 * 60 * 60 * 1000L,
        12 * 60 * 60 * 1000L,
        22 * 60 * 60 * 1000L,
        23 * 60 * 60 * 1000L
    )
    override fun animacionesDePlaga(nivelPlagas: Int): List<Int> {
        // Animaciones específicas para animales con o sin parásitos
        return if (nivelPlagas > 6) {
            listOf(R.drawable.insectos1, R.drawable.insectos2, R.drawable.insectos3, R.drawable.insectos4)
        } else {
            listOf(R.drawable.insectos01, R.drawable.insectos02, R.drawable.insectos03, R.drawable.insectos04)
        }
    }

    override fun animacionesDeBrotes(): List<Int> {
        // Para animales podrías mostrar animaciones de movimiento o energía
        return listOf(R.drawable.brotes1, R.drawable.brotes2, R.drawable.brotes3, R.drawable.brotes4)
    }

    override fun velocidadBrotes(nutrientes: Int): Long {
        // Podría depender del "nivel de energía" en animales
        return if (nutrientes > 6) 80L else 150L
    }

    override fun sonidoCuidado(): Int {
        return R.raw.clic1 // agrega un sonido distinto al de las plantas
    }

}


enum class Etapa {
    SEMBRAR,
    SEMILLA,
    PLANTULA,
    PLANTA,
    MADURA,
    MARCHITA,
    MUERTA
}




