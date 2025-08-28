package com.example.biotamontchi.data

import android.content.Context
import androidx.compose.ui.geometry.Offset


class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("jardin_prefs", Context.MODE_PRIVATE)
    fun guardarPosicionSemilla(posicion: Offset) {
        prefs.edit()
            .putFloat("semilla_x", posicion.x)
            .putFloat("semilla_y", posicion.y)
            .apply()
    }
    fun obtenerPosicionSemilla(): Offset? {
        val x = prefs.getFloat("semilla_x", -1f)
        val y = prefs.getFloat("semilla_y", -1f)
        return if (x >= 0 && y >= 0) Offset(x, y) else null
    }

    fun guardarJuegoIniciado(valor: Boolean) {
        prefs.edit().putBoolean("juegoIniciado", valor).apply()
    }

    fun juegoIniciado(): Boolean {
        return prefs.getBoolean("juegoIniciado", false)
    }

    fun guardarNombreUsuario(nombre: String) {
        val editor = prefs.edit()
        editor.putString("nombre_usuario", nombre)
        editor.apply()
    }

    fun obtenerNombreUsuario(): String? {
        return prefs.getString("nombre_usuario", null)
    }

    fun guardarTipoPlanta(tipo: String) {
        prefs.edit().putString("tipoPlanta", tipo).apply()
    }

    fun obtenerTipoPlanta(): String? = prefs.getString("tipoPlanta", null)

    fun guardarIndicador(nombre: String, valor: Int) {
        prefs.edit().putInt(nombre, valor).apply()
    }
    fun guardarEtapa(etapa: Etapa) {
        val nombre = etapa.name // convierte SEMILLA → "SEMILLA"
        prefs.edit().putString("etapa", nombre).apply()
    }
    fun obtenerEtapa(): Etapa {
        val nombre = prefs.getString("etapa", Etapa.SEMBRAR.name) ?: Etapa.SEMBRAR.name
        return try {
            Etapa.valueOf(nombre)
        } catch (e: IllegalArgumentException) {
            Etapa.SEMBRAR // valor por defecto si hay error
        }
    }

    fun obtenerTexto(clave: String): String? {
        return prefs.getString(clave, null)
    }

    fun obtenerIndicador(nombre: String): Int = prefs.getInt(nombre, 0)

    fun limpiarDatos() {
        prefs.edit().clear().apply()
    }
// PrefsManager.kt

    fun guardarAgua(valor: Int) {
        prefs.edit().putInt("agua", valor).apply()
    }
    fun guardarIndicador2(clave: String, valor: Int) {
        prefs.edit().putInt(clave, valor).apply()
    }
    fun obtenerInt(clave: String): Int {
        return prefs.getInt(clave, 0)
    }

    fun obtenerAgua(): Int {
        return prefs.getInt("agua", 10) // 10 como valor por defecto
    }

    // Guardar y obtener Long
    fun guardarLong(nombre: String, valor: Long) {
        prefs.edit().putLong(nombre, valor).apply()
    }

    fun obtenerLong(nombre: String): Long = prefs.getLong(nombre, 0L)


    fun guardarMonedas(cantidad: Int) {
        prefs.edit().putInt("monedas", cantidad).apply()
    }

    fun guardarTexto(clave: String, valor: String) {
        prefs.edit().putString(clave, valor).apply()
    }
    fun guardarEstado(clave: String, valor: String) {
        prefs.edit().putString(clave, valor).apply()
    }
    fun cargarEstado(clave: String): String {
        return prefs.getString(clave, "normal") ?: "normal"
    }


    fun guardarFloat(clave: String, valor: Float) {
        prefs.edit().putFloat(clave, valor).apply()
    }

    fun obtenerFloat(clave: String): Float? {
        return if (prefs.contains(clave)) prefs.getFloat(clave, 0f) else null
    }

    fun guardarTipoBiotamon(tipo: Int) {
        prefs.edit().putInt("tipo_biotamon", tipo).apply()
    }


    fun guardarEspecie(especie: String) {
        prefs.edit().putString("especie_biotamon", especie).apply()
    }

    fun cargarEspecie(): String {
        return prefs.getString("especie_biotamon", "margarita") ?: "margarita"
    }
    fun guardarPlagasEnPausaHasta(valor: Long) {
        prefs.edit().putLong("plagasEnPausaHasta", valor).apply()
    }

    fun obtenerPlagasEnPausaHasta(): Long {
        return prefs.getLong("plagasEnPausaHasta", 0L)
    }
    fun guardarBrotesEnPausaHasta(valor: Long) {
        prefs.edit().putLong("brotesEnPausaHasta", valor).apply()
    }

    fun obtenerBrotesEnPausaHasta(): Long {
        return prefs.getLong("brotesEnPausaHasta", 0L)
    }
    fun guardarPremioDesbloqueado(idPremio: Int) {
        prefs.edit().putBoolean("premio_$idPremio", true).apply()
    }

    fun premioEstaDesbloqueado(idPremio: Int): Boolean {
        return prefs.getBoolean("premio_$idPremio", false)
    }


    fun guardarMascota(mascota: Mascota) {
        guardarIndicador("agua", mascota.agua)
        guardarIndicador("feliz", mascota.feliz)
        guardarIndicador("nutrientes", mascota.nutrientes)
        guardarIndicador("plagas", mascota.plagas)
        guardarIndicador("resistencia", mascota.resistencia)
        guardarIndicador("germinacion", mascota.germinacion)
        guardarIndicador("acuatica", mascota.acuatica)
        guardarIndicador("aerea", mascota.aerea)
        guardarIndicador("parasita", mascota.parasita)
        guardarIndicador("propagacion", mascota.propagacion)
        guardarIndicador("simbiosis", mascota.simbiosis)
        guardarIndicador("adaptacion", mascota.adaptacion)
        guardarIndicador("riegos", mascota.riegos)
        guardarIndicador("ciclosCompletados", mascota.ciclosCompletados)
        guardarIndicador("indiceAnimacion", mascota.indiceAnimacion)

        guardarLong("fechaInicioJuego", mascota.fechaInicioJuego)
        guardarLong("fechaUltimoRiego", mascota.fechaUltimoRiego)
        guardarLong("tiempoVida", mascota.tiempoVida)
        guardarLong("fechaUltimaFelicidad", mascota.fechaUltimaFelicidad)
        guardarLong("fechaUltimosNutrientes", mascota.fechaUltimosNutrientes)
        guardarLong("fechaUltimasPlagas", mascota.fechaUltimasPlagas)

        guardarTexto("etapa", mascota.etapa.name)
        guardarTexto("etapaMaxima", mascota.etapaMaxima.name)
        guardarIndicador("tipoBiotamon", mascota.tipoBiotamon)
        guardarTexto("especie", mascota.especie)

        guardarEstado("estado", mascota.estado)
        mascota.posicion?.let {
            guardarFloat("posicionX", it.x)
            guardarFloat("posicionY", it.y)
        }
    }



    fun cargarMascota(): Mascota {
        val etapaGuardada = obtenerTexto("etapa")
        val etapa = try {
            Etapa.valueOf(etapaGuardada ?: "SEMBRAR")
        } catch (e: Exception) {
            Etapa.SEMBRAR
        }
        val x = obtenerFloat("semilla_x")
        val y = obtenerFloat("semilla_y")
        val posicion = if (x != null && y != null) Offset(x, y) else null

        return Mascota(
            agua = obtenerIndicador("agua"),
            feliz = obtenerIndicador("feliz"),
            nutrientes = obtenerIndicador("nutrientes"),
            plagas = obtenerIndicador("plagas"),
            resistencia = obtenerIndicador("resistencia"),
            germinacion = obtenerIndicador("germinacion"),
            acuatica = obtenerIndicador("acuatica"),
            aerea = obtenerIndicador("aerea"),
            parasita = obtenerIndicador("parasita"),
            propagacion = obtenerIndicador("propagacion"),
            simbiosis = obtenerIndicador("simbiosis"),
            adaptacion = obtenerIndicador("adaptacion"),
            fechaInicioJuego = obtenerLong("fechaInicioJuego"),
            fechaUltimoRiego = obtenerLong("fechaUltimoRiego"),
            tiempoVida = obtenerLong("tiempoVida"),
            ciclosCompletados = obtenerIndicador("ciclosCompletados"),
            indiceAnimacion = obtenerIndicador("indiceAnimacion"),
            etapa = etapa, // ✅ aquí cargas correctamente la etapa guardada
            riegos = obtenerIndicador("riegos"),
            posicion = posicion,
            tipoBiotamon = obtenerIndicador("tipoBiotamon"),
            especie = cargarEspecie(),
            estado= cargarEstado("estado"),
            causaMuerte = obtenerCausaMuerte()
        )
    }

    fun guardarSonidoActivado(activo: Boolean) {
        prefs.edit().putBoolean("sonido_activado", activo).apply()
    }

    fun sonidoActivado(): Boolean {
        return prefs.getBoolean("sonido_activado", true)
    }

    fun guardarVolumen(volumen: Float) {
        prefs.edit().putFloat("volumen_general", volumen).apply()
    }

    fun obtenerVolumen(): Float {
        return prefs.getFloat("volumen_general", 1f)
    }

    // Guardar puntos
    fun guardarPuntos(puntos: Int) {
        prefs.edit().putInt("puntos", puntos).apply()
    }

    // Obtener puntos
    fun obtenerPuntos(): Int {
        return prefs.getInt("puntos", 0) // default 0 si no hay nada guardado
    }

    // Incrementar puntos
    fun sumarPuntos(cantidad: Int = 1): Int {
        val puntosActuales = obtenerPuntos()
        val nuevosPuntos = puntosActuales + cantidad
        guardarPuntos(nuevosPuntos)
        return nuevosPuntos
    }

    // Resetear puntos (cuando muere la planta y se reinicia)
    fun resetearPuntos() {
        guardarPuntos(0)
    }
    fun guardarCausaMuerte(causa: CausaMuerte) {
        prefs.edit().putString("causaMuerte", causa.name).apply()
    }

    fun obtenerCausaMuerte(): CausaMuerte {
        val s = prefs.getString("causaMuerte", CausaMuerte.NINGUNA.name) ?: CausaMuerte.NINGUNA.name
        return runCatching { CausaMuerte.valueOf(s) }.getOrElse { CausaMuerte.NINGUNA }
    }

    fun resetearCausaMuerte() {
        guardarCausaMuerte(CausaMuerte.NINGUNA)
    }

    fun guardarHistorialRiegos(historial: List<Long>) {
        val serializado = historial.joinToString(",")
        prefs.edit().putString("historial_riegos", serializado).apply()
    }

    fun obtenerHistorialRiegos(): MutableList<Long> {
        val serializado = prefs.getString("historial_riegos", "") ?: ""
        return if (serializado.isNotEmpty()) {
            serializado.split(",").mapNotNull { it.toLongOrNull() }.toMutableList()
        } else {
            mutableListOf()
        }
    }


}
