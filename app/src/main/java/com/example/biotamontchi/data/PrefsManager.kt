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


    fun guardarNombreUsuario(nombre: String) {
        prefs.edit().putString("nombreUsuario", nombre).apply()
    }

    fun obtenerNombreUsuario(): String? = prefs.getString("nombreUsuario", null)

    fun guardarTipoPlanta(tipo: String) {
        prefs.edit().putString("tipoPlanta", tipo).apply()
    }

    fun obtenerTipoPlanta(): String? = prefs.getString("tipoPlanta", null)

    fun guardarIndicador(nombre: String, valor: Int) {
        prefs.edit().putInt(nombre, valor).apply()
    }

    fun obtenerTexto(clave: String): String? {
        return prefs.getString(clave, null)
    }

    fun obtenerIndicador(nombre: String): Int = prefs.getInt(nombre, 0)

    fun limpiarDatos() {
        prefs.edit().clear().apply()
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
        guardarLong("fechaInicioJuego", mascota.fechaInicioJuego)
        guardarLong("fechaUltimoRiego", mascota.fechaUltimoRiego)
        guardarLong("tiempoVida", mascota.tiempoVida)
        guardarIndicador("ciclosCompletados", mascota.ciclosCompletados)
        guardarIndicador("indiceAnimacion", mascota.indiceAnimacion)

        guardarTexto("etapa", mascota.etapa.name) // ✅ guardamos la etapa
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
            especie = cargarEspecie()

        )
    }
}
    /*
    fun guardarMascota(mascota: MascotaBase) {
        // Asegúrate de hacer un "cast seguro" si necesitas acceder a propiedades específicas
        val planta = mascota as? MascotaPlanta ?: return

        guardarIndicador("agua", planta.agua)
        guardarIndicador("feliz", planta.feliz)
        guardarIndicador("nutrientes", planta.nutrientes)
        guardarIndicador("plagas", planta.plagas)
        guardarIndicador("resistencia", planta.resistencia)
        guardarIndicador("germinacion", planta.germinacion)
        guardarIndicador("acuatica", planta.acuatica)
        guardarIndicador("aerea", planta.aerea)
        guardarIndicador("parasita", planta.parasita)
        guardarIndicador("propagacion", planta.propagacion)
        guardarIndicador("simbiosis", planta.simbiosis)
        guardarIndicador("adaptacion", planta.adaptacion)
        guardarIndicador("riegos", planta.riegos)
        guardarLong("fechaInicioJuego", planta.fechaInicioJuego)
        guardarLong("fechaUltimoRiego", planta.fechaUltimoRiego)
        guardarLong("tiempoVida", planta.tiempoVida)
        guardarIndicador("ciclosCompletados", planta.ciclosCompletados)
        guardarIndicador("indiceAnimacion", planta.indiceAnimacion)

        guardarTexto("etapa", planta.etapa.name)
        guardarTexto("especie_biotamon", planta.especie)
        guardarIndicador("tipoBiotamon", planta.tipoBiotamon)
    }

    fun cargarMascota(): MascotaBase {  // ✅ cambia el tipo de retorno a MascotaBase
        val etapaGuardada = obtenerTexto("etapa")
        val etapa = try {
            Etapa.valueOf(etapaGuardada ?: "SEMBRAR")
        } catch (e: Exception) {
            Etapa.SEMBRAR
        }
        val x = obtenerFloat("semilla_x")
        val y = obtenerFloat("semilla_y")
        val posicion = if (x != null && y != null) Offset(x, y) else null

        return MascotaPlanta( // ✅ crea instancia de MascotaPlanta directamente
            agua = obtenerIndicador("agua"),
            feliz = obtenerIndicador("feliz"),
            fechaUltimaFelicidad = obtenerLong("fechaUltimaFelicidad"),
            nutrientes = obtenerIndicador("nutrientes"),
            fechaUltimosNutrientes = obtenerLong("fechaUltimosNutrientes"),
            plagas = obtenerIndicador("plagas"),
            fechaUltimasPlagas = obtenerLong("fechaUltimasPlagas"),
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
            etapa = etapa,
            riegos = obtenerIndicador("riegos"),
            posicion = posicion,
            tipoBiotamon = obtenerIndicador("tipoBiotamon"),
            especie = cargarEspecie()
        )
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
        guardarLong("fechaInicioJuego", mascota.fechaInicioJuego)
        guardarLong("fechaUltimoRiego", mascota.fechaUltimoRiego)
        guardarLong("tiempoVida", mascota.tiempoVida)
        guardarIndicador("ciclosCompletados", mascota.ciclosCompletados)
        guardarIndicador("indiceAnimacion", mascota.indiceAnimacion)

        guardarTexto("etapa", mascota.etapa.name) // ✅ guardamos la etapa
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
            especie = cargarEspecie()

        )
    }
*/

