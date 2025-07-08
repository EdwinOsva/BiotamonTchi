package com.example.biotamontchi.data


import androidx.compose.ui.geometry.Offset
abstract class MascotaBase(
    open val datos: Mascota
) {
    abstract val tipo: String
    open val umbrales: List<Long> = listOf(
        15 * 1000L,
        10 * 60 * 1000L,
        15 * 60 * 1000L,
        25 * 60 * 1000L,
        40 * 60 * 1000L,
        45 * 60 * 1000L,
    )
    open fun determinarEtapa(): Etapa {
        val tiempo = System.currentTimeMillis() - datos.fechaInicioJuego

        return when {
            tiempo < umbrales[0] -> Etapa.SEMBRAR
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
