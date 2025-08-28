package com.example.biotamontchi.data


abstract class MascotaBase(
    open val datos: Mascota
) {
    abstract val tipo: String
    open val umbrales: List<Long> = listOf(
        15 * 1000L,
        1 * 60 * 60 * 1000L,
        4 * 60 * 60 * 1000L,
        9 * 60 * 60 * 1000L,
        18 * 60 * 60 * 1000L,
        27 * 60 * 60 * 1000L
    )
    open fun determinarEtapa(etapaActual: Etapa = Etapa.SEMBRAR): Etapa {
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





    abstract fun animacionesDePlaga(nivelPlagas: Int = datos.plagas): List<Int>
    abstract fun animacionesDeBrotes(): List<Int>
    abstract fun velocidadBrotes(nutrientes: Int = datos.nutrientes): Long
    abstract fun sonidoCuidado(): Int

     // ✅ Aquí solo la declaras
}
fun reconstruirMascotaBase(mascota: Mascota): MascotaBase {
    return when (mascota.tipoBiotamon) {
        1 -> MascotaPlanta(mascota)
        2 -> MascotaAnimal(mascota)
        else -> MascotaPlanta(mascota) // fallback
    }
}
