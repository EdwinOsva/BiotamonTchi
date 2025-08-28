package com.example.biotamontchi.ui.screens

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.biotamontchi.ui.components.FondoDinamico
import com.example.biotamontchi.viewmodel.VistaHoraReloj
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import com.example.biotamontchi.data.Etapa
import com.example.biotamontchi.data.Mascota
import com.example.biotamontchi.ui.components.DibujarAnimacionBrotes
import com.example.biotamontchi.ui.components.DibujarAnimacionRiego
import com.example.biotamontchi.ui.components.DibujarAnimacionSembrar
import com.example.biotamontchi.ui.components.DibujarPersonajeAnimacionLoop
import com.example.biotamontchi.ui.components.IndicadoresSuperpuestos
import com.example.biotamontchi.ui.components.PanelAlimentos
import com.example.biotamontchi.ui.components.PanelAtributos
import com.example.biotamontchi.ui.components.PanelHerramientas
import com.example.biotamontchi.ui.components.PanelPremios
import com.example.biotamontchi.ui.components.calcularFelicidadDesdeUltimoValor
import com.example.biotamontchi.ui.components.calcularNivelAgua
import com.example.biotamontchi.ui.components.calcularNivelDesdeUltimoValor
import com.example.biotamontchi.ui.components.calcularNivelDesdeUltimoValor2
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biotamontchi.data.FrutaEnPantalla
import com.example.biotamontchi.data.MascotaAnimal
import com.example.biotamontchi.data.MascotaPlanta
import com.example.biotamontchi.data.PrefsManager
import com.example.biotamontchi.data.detectaColisionConPersonaje
import com.example.biotamontchi.model.GameAudioViewModelFactory
import com.example.biotamontchi.ui.components.DibujarAnimacionPlagas
import com.example.biotamontchi.ui.components.RelojConControles
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.biotamontchi.data.PremioEnPantalla
import com.example.biotamontchi.data.detectaColisionConRegresar
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.DpSize
import com.example.biotamontchi.data.AnimarPapaloteZigZag
import com.example.biotamontchi.data.AnimarPelotaRebotando
import com.example.biotamontchi.data.AnimarSaxofonFantastico
import com.example.biotamontchi.data.CausaMuerte
import com.example.biotamontchi.data.DibujarAnimacionPremio
import com.example.biotamontchi.data.TipoPremio
import com.example.biotamontchi.data.animacionesPremios
import com.example.biotamontchi.data.detectaColisionEntrePremios
import com.example.biotamontchi.data.generarTrayectoriaPapalote
import com.example.biotamontchi.data.generarTrayectoriaSinRebotes
import com.example.biotamontchi.model.ControlVolumenDrag
import com.example.biotamontchi.model.GameAudioViewModel2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlin.math.sqrt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(viewModelHora: VistaHoraReloj, onRegresarClick: () -> Unit ) {
    // Context y preferencias
    val context2 = LocalContext.current.applicationContext as Application


    val audioViewModel2: GameAudioViewModel2 = viewModel(factory = GameAudioViewModelFactory(context2.applicationContext as Application))

    val horaActual by viewModelHora.estadoHora.collectAsState()
    val context = LocalContext.current
    val prefs = remember { PrefsManager(context) }
    // Nombre de usuario y tipo de planta
    val nombreUsuario = remember { mutableStateOf(prefs.obtenerNombreUsuario()) }
    var tipoPlanta = remember { mutableStateOf(prefs.obtenerTipoPlanta()) }
    //cargar mascota
    var mascotaGuardada = prefs.cargarMascota()





    var plantaSeleccionada by remember {
        mutableStateOf(mascotaGuardada.especie.replaceFirstChar { it.uppercase() })
    }
    val mostrarDialogo =
        remember { mutableStateOf(nombreUsuario.value == null || tipoPlanta.value == null) }
    // Monedas
    val monedas = remember {
        mutableStateOf(
            context.getSharedPreferences("datosMascota", Context.MODE_PRIVATE)
                .getInt("monedas", 0)
        )
    }
    // Estado de la UI
    var mostrarPanel by remember { mutableStateOf(false) }
    var mostrarPanelHerramientas by remember { mutableStateOf(false) }
    var mostrarPanelPremios by remember { mutableStateOf(false) }
    var mostrarPanelAlimentos by remember { mutableStateOf(false) }
    var mostrarAnimacionRiego by remember { mutableStateOf(false) }
    //tutorial
    var enTutorial by remember { mutableStateOf(false) }
    var pasoTutorial by remember { mutableStateOf(0) }
    //saludos
    var mostrarSaludo by remember { mutableStateOf(true) }
//posicion inicial al centro del personaje
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current
    val centroOffset = with(density) {
        Offset(
            x = screenWidth.toPx() / 2,
            y = screenHeight.toPx() / 2
        )
    }
    val posicionGuardada = remember {
        val x = prefs.obtenerFloat("semilla_x")
        val y = prefs.obtenerFloat("semilla_y")
        if (x != null && y != null) Offset(x, y) else centroOffset
    }
// Estado de la posición de la semilla
    var posicionPersonaje by remember { mutableStateOf(posicionGuardada) }
    var semillaPosicion by remember { mutableStateOf(posicionGuardada) }
    val juegoIniciado = remember { prefs.juegoIniciado() }
//mostrar horas del reloj
    val hora12 = if (horaActual.hour % 12 == 0) 12 else horaActual.hour % 12

//mostrar valores de humedad y riego

    val ahora = System.currentTimeMillis()  // 1. Obtener tiempo actual


    val fechaUltimoRiego = mascotaGuardada.fechaUltimoRiego  // 3. Fecha último riego de la mascota cargada

    val intervalo = 1 * 60 * 60 * 1000L  // 4. Intervalo para cálculo (1 hora)

    val tiempoTranscurridoIndicador = ahora - fechaUltimoRiego  // 5. Calcular tiempo transcurrido

    val porcentaje = tiempoTranscurridoIndicador.toFloat() / intervalo.toFloat()  // 6. Porcentaje para bajar nivel agua

    val nivelAguaSaludo = when {  // 7. Nivel agua para saludo (puede ser opcional)
        porcentaje >= 1f -> 0
        porcentaje < 0f -> 10
        else -> 10 - (porcentaje * 10).toInt()
    }

    val nivelAguaInicial = calcularNivelAgua(prefs)  // 8. Calcular nivel de agua real (basado en prefs)

    val estadoInicial = if (nivelAguaSaludo <= 4) "seco" else "normal"  // 9. Estado inicial según agua

    var mascotaEstado by remember { mutableStateOf(mascotaGuardada.copy(agua = nivelAguaInicial)) }  // 10. Estado mutable de la mascota con agua actualizada

    var estadoMascota by remember { mutableStateOf(estadoInicial) }  // 11. Estado visual mutable ("seco"/"normal")

    var tiempoTranscurrido by remember { mutableStateOf(0L) }  // 12. Estado para tiempo transcurrido (puede usarlo para animaciones u otra lógica)





//saludo inicio

    var mostrarMensajeInicio by remember { mutableStateOf(false) }
// determinar etapa para mostrar dibujos en animacion
    val mascotaBase = when (mascotaEstado.tipoBiotamon) {
        1 -> MascotaPlanta(mascotaEstado)
        2 -> MascotaAnimal(mascotaEstado)
        else -> MascotaPlanta(mascotaEstado)
    }
    val etapaInicial by remember(mascotaEstado) {
        mutableStateOf(
            if (mascotaBase.datos.riegos == 0) {
                Etapa.SEMBRAR
            } else {
                mascotaBase.determinarEtapa()
            }
        )
    }
    var etapaActual by remember { mutableStateOf(etapaInicial) }
    var nuevaEtapa by remember { mutableStateOf(etapaInicial) }
    var mostrarCambioEtapa by remember { mutableStateOf(false) }
    var mostrarResumenMuerte by remember { mutableStateOf(false) }
    var resumenMostrado by remember { mutableStateOf(false) }
    var tipoGeneralSeleccionado by remember { mutableStateOf("Planta") }
//inicializacion de mascota por tipo y especie
    var tipoBiotamon by remember {
        mutableStateOf(
            when (tipoGeneralSeleccionado) {
                "Planta" -> 1
                "Animal" -> 2
                "Roca" -> 3
                else -> 1
            }
        )
    }
    val mapaTipoBiotamon = mapOf(
        1 to "Planta",
        2 to "Animal",
        3 to "Roca"
    )
    val tipoBiotamonNombre = mapaTipoBiotamon[tipoBiotamon] ?: "Planta"
    var mostrarDialogoCambioBiotamon by remember { mutableStateOf(false) }
    val opcionesTipoBiotamon = listOf("Planta", "Animal", "Roca")
    val opcionesTipoPlanta = listOf("Margarita", "Amapola", "Lili")
    var objetivo by remember { mutableStateOf<Offset?>(null) }




//indicadores
// NUTRIENTES
    var brotesEnPausaHasta by remember { mutableStateOf(0L) }
    val valorNutrientes = prefs.obtenerIndicador("nutrientes")
    val fechaUltimosNutrientes = prefs.obtenerLong("fechaUltimosNutrientes")
    val nutrientesCalculados = calcularNivelDesdeUltimoValor(
        valorGuardado = valorNutrientes,
        fechaUltima = fechaUltimosNutrientes,
        ahora = ahora,
        intervalo = 1000 * 60 * 10L // 🔁 cada 10 minutos baja 1 punto si la app está cerrada
    )
    mascotaEstado = mascotaEstado.copy(
        nutrientes = nutrientesCalculados
    )
    //feliz
    val valorFeliz = prefs.obtenerIndicador("feliz") // o mascota.feliz
    val fechaUltimaFelicidad = prefs.obtenerLong("fechaUltimaFelicidad")
    val felicidadCalculada = calcularFelicidadDesdeUltimoValor(
        valorGuardado = valorFeliz,
        fechaUltima = fechaUltimaFelicidad,
        ahora = ahora
    )

    mascotaEstado = mascotaEstado.copy(
        feliz = felicidadCalculada
    )
    //plagas
    val valorPlagas = prefs.obtenerIndicador("plagas")
    val fechaUltimasPlagas = prefs.obtenerLong("fechaUltimasPlagas")
// ⏸️ Cargar la pausa desde prefs
    var plagasEnPausaHasta = prefs.obtenerLong("plagasEnPausaHasta")
    val pausaActiva = ahora < plagasEnPausaHasta
// Si está en pausa, no aumentan plagas
    val plagasCalculadas = if (pausaActiva) {
        valorPlagas
    } else {
        calcularNivelDesdeUltimoValor2(
            valorGuardado = valorPlagas,
            fechaUltima = fechaUltimasPlagas,
            ahora = ahora,
            intervalo = 1000 * 60 * 10L // cada 10 min sube 1 punto
        )
    }
// Actualizar el estado
    mascotaEstado = mascotaEstado.copy(plagas = plagasCalculadas)
//animaciones plagas y brotes
    var mostrarAnimacionPlagas by remember { mutableStateOf(false) }
    var mostrarAnimacionBrotes by remember { mutableStateOf(false) }
    val delayIndicadorNutrientes = 30_000L
    var muerteProcesada by remember { mutableStateOf(false) }
    prefs.guardarIndicador("agua", nivelAguaInicial)
    prefs.guardarIndicador("nutrientes", nutrientesCalculados)
    prefs.guardarIndicador("feliz", felicidadCalculada)
    prefs.guardarIndicador("plagas", plagasCalculadas)
    //alimentos en pantalla
    val frutasEnPantalla = remember { mutableStateListOf<FrutaEnPantalla>() }
    val frutasAEliminar = remember { mutableStateListOf<FrutaEnPantalla>() }

    //premios
    val premiosEnPantalla = remember { mutableStateListOf<PremioEnPantalla>() }
    val premiosAEliminar = remember { mutableStateListOf<PremioEnPantalla>() }
    val posicionBotonRegresar = remember { mutableStateOf(Offset.Zero) }
    val animacionesActivas = remember { mutableStateListOf<PremioEnPantalla>() }
    val mostrarVolumen = remember { mutableStateOf(false) }
//pantalla insectos

    var mostrarPantallaInspeccion by rememberSaveable { mutableStateOf(false) }
    var animacionPelotaActiva by remember { mutableStateOf<PremioEnPantalla?>(null) }
    var animacionPelotaBateada by remember { mutableStateOf<PremioEnPantalla?>(null) }
    var puntosPelotaRebote by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var tortugaActiva by remember { mutableStateOf<PremioEnPantalla?>(null) }
    var direccionTortuga by remember { mutableStateOf(1) } // 1: derecha, -1: izquierda
    var posicionTortuga by remember { mutableStateOf(Offset(100f, 500f)) } // posición inicial
var papaloteActivo by remember { mutableStateOf<PremioEnPantalla?>(null) }
    var saxofonActivo by remember { mutableStateOf<PremioEnPantalla?>(null) }

    //minujuegos
    var mostrarPantallaPPT by rememberSaveable { mutableStateOf(false) }
    var mostrarPantallaMemorama by rememberSaveable { mutableStateOf(false) }
    var mostraPantallaPacBug by rememberSaveable { mutableStateOf(false) }

    // Cargar puntos guardados al iniciar
    mascotaEstado.puntos = prefs.obtenerPuntos()

    //muertes

// Estado para muerte por descuido

    // Lista de riegos recientes (se puede restaurar desde prefs al crear la mascota)
    var historialRiegos: MutableList<Long> = prefs.obtenerHistorialRiegos()
    var mostrarResumenMuerteDescuido by remember { mutableStateOf(false) }
    var muerteProcesadaDescuido by remember { mutableStateOf(false) }

    val TIEMPO_MUERTE: Long = 72 * 60 * 60 * 1000L // 24h en ms

// 🔹 Agua

    val tiempoEnCeroAgua = if (mascotaEstado.agua == 0) ahora - fechaUltimoRiego else 0L
    val murioPorSequia = tiempoEnCeroAgua >= TIEMPO_MUERTE

    if (murioPorSequia && mascotaEstado.causaMuerte == CausaMuerte.NINGUNA) {
        val nueva = mascotaEstado.copy(
            etapa = Etapa.MUERTA,
            causaMuerte = CausaMuerte.DESCUIDO_SEQUIA
        )
        mascotaEstado = nueva
        etapaActual = Etapa.MUERTA
        nuevaEtapa = Etapa.MUERTA

        // Persistimos lo esencial
        prefs.guardarEtapa(Etapa.MUERTA)
        prefs.guardarCausaMuerte(CausaMuerte.DESCUIDO_SEQUIA)

        // Abre solo el diálogo de descuido
        mostrarResumenMuerteDescuido = true
    }

// 🔹 Nutrientes
   /* val tiempoSinNutrientes = if (nutrientesCalculados == 0) ahora - fechaUltimosNutrientes else 0L
    val murioPorFaltaNutrientes = tiempoSinNutrientes >= TIEMPO_MUERTE
    if (murioPorFaltaNutrientes && !muerteProcesadaDescuido){
        nuevaEtapa = Etapa.MUERTA
        etapaActual = Etapa.MUERTA
        mascotaEstado = mascotaEstado.copy(etapa = Etapa.MUERTA)
        prefs.guardarEtapa(Etapa.MUERTA)
        mostrarResumenMuerteDescuido = true
    }*/

    val tiempoSinNutrientes = if (nutrientesCalculados == 0) ahora - fechaUltimosNutrientes else 0L
    val murioPorFaltaNutrientes = tiempoSinNutrientes >= TIEMPO_MUERTE

    if (murioPorFaltaNutrientes && mascotaEstado.causaMuerte == CausaMuerte.NINGUNA) {
        val nueva = mascotaEstado.copy(
            etapa = Etapa.MUERTA,
            causaMuerte = CausaMuerte.DESCUIDO_NUTRIENTES
        )
        mascotaEstado = nueva
        etapaActual = Etapa.MUERTA
        nuevaEtapa = Etapa.MUERTA

        // Persistimos lo esencial
        prefs.guardarEtapa(Etapa.MUERTA)
        prefs.guardarCausaMuerte(CausaMuerte.DESCUIDO_NUTRIENTES)

        // Abre solo el diálogo de descuido
        mostrarResumenMuerteDescuido = true
    }





// 🔹 Plagas
   /* val tiempoPlagasCritico = if (plagasCalculadas >= 10) ahora - fechaUltimasPlagas else 0L
    val murioPorPlagas = tiempoPlagasCritico >= TIEMPO_MUERTE
    if (murioPorPlagas && !muerteProcesadaDescuido) {
        nuevaEtapa = Etapa.MUERTA
        etapaActual = Etapa.MUERTA
        mascotaEstado = mascotaEstado.copy(etapa = Etapa.MUERTA)
        prefs.guardarEtapa(Etapa.MUERTA)

        mostrarResumenMuerteDescuido = true

    }*/

    val tiempoPlagasCritico = if (plagasCalculadas == 0) ahora - fechaUltimosNutrientes else 0L
    val murioPorPlagas = tiempoPlagasCritico >= TIEMPO_MUERTE

    if (murioPorPlagas  && mascotaEstado.causaMuerte == CausaMuerte.NINGUNA) {
        val nueva = mascotaEstado.copy(
            etapa = Etapa.MUERTA,
            causaMuerte = CausaMuerte.DESCUIDO_PLAGAS
        )
        mascotaEstado = nueva
        etapaActual = Etapa.MUERTA
        nuevaEtapa = Etapa.MUERTA

        // Persistimos lo esencial
        prefs.guardarEtapa(Etapa.MUERTA)
        prefs.guardarCausaMuerte(CausaMuerte.DESCUIDO_PLAGAS)

        // Abre solo el diálogo de descuido
        mostrarResumenMuerteDescuido = true
    }

//nivel de agricultor
    val listaDeLogros = listOf(
        "búscador de la técnica",
        "Conquistador de la germinación",
        "Cazador de flores",
        "Explorador de la tierra",
        "Instructor del ciclo",
        "Agricultor de buena mano",
        "Aliado de los bosques",
        "Creador de humus",
        "Mejorador Vegetal",
        "Agricultor Cósmico",
        "Conquistador del suelo",
        "Semillas de oro",
        "Portador de nuevas variedades",
        "Fitomejorador fuerte",
        "Manos de poder",
        "Agricultor de lo inexplicable",
        "Agricultor Solitario",
        "Granjero de leyenda",
        "Agricultor de orgullo",
        "Manos hábiles",
        "Productor de alimentos",
        "Maestro en las Semillas",
        "Botánico silencioso",
        "Gran Granjero",
        "Maestro Agrícola",
        "Granjero de Damasco",
        "Sacudidor de la tierra",
        "Adicto Fotosíntetico",
        "Acolito del Carbohidrato",
        "Regenerador de vida",
        "Sembrador de cielos",
        "El Experto de las plantas"
    )




    fun registrarRiego() {
        val ahora = System.currentTimeMillis()
        historialRiegos.add(ahora)
        if (historialRiegos.size > 10) {
            historialRiegos.removeAt(0) // mantener solo los últimos 10
        }
        prefs.guardarHistorialRiegos(historialRiegos)
    }

    fun revisarExcesoAgua(intervaloNormal: Long = 60 * 60 * 1000) { // 1 hora
        if (historialRiegos.size >= 5) {
            val ultimosCinco = historialRiegos.takeLast(5)
            val tiempoEntrePrimeroYUltimo = ultimosCinco.last() - ultimosCinco.first()

            val limite = intervaloNormal / 2 // media hora

            if (tiempoEntrePrimeroYUltimo < limite) {
                // ☠️ muerte por exceso de agua
               nuevaEtapa=Etapa.MUERTA
                mostrarResumenMuerteDescuido = true
                muerteProcesadaDescuido = true
            }
        }
    }


    // Función para actualizar monedas y guardar en preferencias
    fun actualizarMonedas(nuevaCantidad: Int) {
        monedas.value = nuevaCantidad
        val editor = context.getSharedPreferences("datosMascota", Context.MODE_PRIVATE).edit()
        editor.putInt("monedas", nuevaCantidad)
        editor.apply()
    }
    fun iniciarNuevaPartida(prefs: PrefsManager, nombre: String, tipo: String, planta: String) {
        val ahora = System.currentTimeMillis()
        val tipoBiotamon = when (tipo) {
            "Planta" -> 1
            "Animal" -> 2
            "Roca" -> 3
            else -> 1
        }
        // Guardar nombre y tipo
        prefs.guardarNombreUsuario(nombre)
        prefs.guardarTipoPlanta(tipo)
        prefs.guardarEspecie(planta.lowercase())
        // Reiniciar indicadores
        val indicadoresIniciales = listOf(
            "agua", "felíz", "nutrientes", "plagas",
            "resistencia", "germinación", "acuática", "aérea",
            "parásita", "propagación", "simbiósis", "adaptación",
            "riegos", "ciclosCompletados", "indiceAnimacion"
        )
        indicadoresIniciales.forEach {
            prefs.guardarIndicador(it, 0)
        }
        // Crear nueva mascota
        val nuevaMascota = Mascota(
            agua = 0,
            feliz = 0,
            fechaUltimaFelicidad = 0L,
            nutrientes = 0,
            fechaUltimosNutrientes = 0L,
            plagas = 0,
            fechaUltimasPlagas = 0L,
            resistencia = 0,
            germinacion = 0,
            acuatica = 0,
            aerea = 0,
            parasita = 0,
            propagacion = 0,
            simbiosis = 0,
            adaptacion = 0,
            fechaInicioJuego = ahora,
            fechaUltimoRiego = 0L,
            tiempoVida = 0L,
            ciclosCompletados = 0,
            indiceAnimacion = 0,
            etapa = Etapa.SEMBRAR,
            riegos = 0,
            etapaMaxima = Etapa.SEMBRAR,
            tipoBiotamon = tipoBiotamon,
            especie = planta.lowercase(),
            puntos = 0
        )
        prefs.resetearCausaMuerte()
        prefs.resetearPuntos()
        mostrarAnimacionPlagas = false
        mostrarAnimacionBrotes = false
        mascotaEstado.plagas = 0
        mascotaEstado.feliz = 0
        mascotaEstado.nutrientes = 0
        mascotaEstado.fechaUltimaFelicidad = 0L
        mascotaEstado.fechaUltimosNutrientes = 0L
        mascotaEstado.fechaUltimasPlagas = 0L
        prefs.guardarJuegoIniciado(false)
        etapaActual = Etapa.SEMBRAR
        nuevaEtapa = Etapa.SEMBRAR
        // Posición inicial al centro
        val centroOffset = with(density) {
            Offset(screenWidth.toPx() / 2, screenHeight.toPx() / 2)
        }
        prefs.guardarPosicionSemilla(centroOffset)
        posicionPersonaje = centroOffset
        semillaPosicion = centroOffset
        // Guardar mascota y monedas
        prefs.guardarMascota(nuevaMascota)
        prefs.guardarMonedas(0)
        // Reiniciar estado de tutorial
        enTutorial = true
        pasoTutorial = 0
        tiempoTranscurrido = 0L
//si murio por seco u otra
        muerteProcesada = true
        mostrarDialogoCambioBiotamon = false
        mostrarResumenMuerte = false
        mascotaEstado = nuevaMascota
        mascotaGuardada = mascotaEstado
        // Forzar actualización de etapa y base
        val nuevaMascota2 = prefs.cargarMascota()
        mascotaEstado = nuevaMascota2
        mascotaGuardada = nuevaMascota2

        val mascotaBaseActualizada = when (nuevaMascota.tipoBiotamon) {
            1 -> MascotaPlanta(nuevaMascota)
            2 -> MascotaAnimal(nuevaMascota)
            else -> MascotaPlanta(nuevaMascota)
        }
        nuevaEtapa = mascotaBaseActualizada.determinarEtapa()
        etapaActual = nuevaEtapa
    }

    //cambio de especie o mascota
    if (mostrarDialogoCambioBiotamon) {
        MostrarDialogoCambioBiotamon(
            tipoActual = tipoBiotamonNombre,
            plantaActual = plantaSeleccionada,
            opcionesTipoBiotamon = opcionesTipoBiotamon,
            opcionesTipoPlanta = opcionesTipoPlanta,
            onConfirmar = { nuevoTipoNombre, nuevaPlanta ->
                tipoBiotamon = mapaTipoBiotamon.entries.first { it.value == nuevoTipoNombre }.key
                tipoGeneralSeleccionado = nuevoTipoNombre
                plantaSeleccionada = nuevaPlanta
                mascotaEstado = mascotaEstado.copy(
                    especie = nuevaPlanta.lowercase(), // ✅ ACTUALIZA ESPECIE AQUÍ
                    ciclosCompletados = mascotaEstado.ciclosCompletados + 1,
                    fechaInicioJuego = System.currentTimeMillis(),
                    etapa = Etapa.SEMBRAR
                )
                etapaActual = Etapa.SEMBRAR
                nuevaEtapa = Etapa.SEMBRAR
                // ✅ GUARDAMOS CORRECTAMENTE LOS DATOS
                prefs.guardarMascota(mascotaEstado)
                prefs.guardarEspecie(nuevaPlanta.lowercase())
                prefs.guardarTipoPlanta(nuevaPlanta.lowercase())
                prefs.guardarTipoBiotamon(tipoBiotamon)
                muerteProcesada = true
                mostrarDialogoCambioBiotamon = false
                mostrarResumenMuerte = false
                mascotaGuardada = mascotaEstado
                // Forzar actualización de etapa y base
                val nuevaMascota = prefs.cargarMascota()
                mascotaEstado = nuevaMascota
                mascotaGuardada = nuevaMascota
                val mascotaBaseActualizada = when (nuevaMascota.tipoBiotamon) {
                    1 -> MascotaPlanta(nuevaMascota)
                    2 -> MascotaAnimal(nuevaMascota)
                    else -> MascotaPlanta(nuevaMascota)
                }
                nuevaEtapa = mascotaBaseActualizada.determinarEtapa()
                etapaActual = nuevaEtapa
            },
            onCancelar = {
                mostrarDialogoCambioBiotamon = false
                muerteProcesada = false
            }
        )
    }

    //muerte por viejo, ciclo normal, usuario no ha perdido


    if (mostrarResumenMuerte) {
        val tiempoVidaMs = mascotaEstado.tiempoVida.takeIf { it > 0 }
            ?: (System.currentTimeMillis() - mascotaEstado.fechaInicioJuego)
        val minutosVida = (tiempoVidaMs / 1000 / 60).toInt()
        val horas = minutosVida / 60
        val minutos = minutosVida % 60
        AlertDialog(
            onDismissRequest = {
                mostrarResumenMuerte = false
            },
            confirmButton = {
                TextButton(onClick = {
                    // ✅ Reiniciar con misma especie
                    mostrarResumenMuerte = false
                    mostrarDialogoCambioBiotamon = false
                    muerteProcesada = true
                    mascotaEstado = mascotaEstado.copy(
                        ciclosCompletados = mascotaEstado.ciclosCompletados + 1,
                        fechaInicioJuego = System.currentTimeMillis(),
                        etapa = Etapa.SEMBRAR
                    )
                    etapaActual = Etapa.SEMBRAR
                    nuevaEtapa = Etapa.SEMBRAR
                    prefs.guardarMascota(mascotaEstado)
                    mascotaGuardada = mascotaEstado
                    // Forzar actualización de etapa y base
                    val nuevaMascota = prefs.cargarMascota()
                    mascotaEstado = nuevaMascota
                    mascotaGuardada = nuevaMascota
                    val mascotaBaseActualizada = when (nuevaMascota.tipoBiotamon) {
                        1 -> MascotaPlanta(nuevaMascota)
                        2 -> MascotaAnimal(nuevaMascota)
                        else -> MascotaPlanta(nuevaMascota)
                    }
                    nuevaEtapa = mascotaBaseActualizada.determinarEtapa()
                    etapaActual = nuevaEtapa
                }) {
                    Text("Resembrar especie")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        // 🔄 Cambiar especie
                        mostrarResumenMuerte = false
                        muerteProcesada = true
                        mostrarDialogoCambioBiotamon = true
                    }) {
                        Text("Cambiar especie")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        // ❌ Cancelar (volver a mostrar después con delay)
                        mostrarResumenMuerte = false
                        muerteProcesada = false
                    }) {
                        Text("Cancelar")
                    }
                }
            },
            title = { Text("☠️ Tu planta ha muerto de vieja") },
            text = {
                Text(
                    "Resumen de tu planta, ${nombreUsuario.value}:\n\n" +
                            "🌼 Especie de Mascota: ${tipoPlanta.value}\n" +
                            "🌿 Riegos totales: ${mascotaEstado.riegos}\n" +
                            "🔁 Ciclos completados: ${mascotaEstado.ciclosCompletados}\n" +
                            "⏳ Tiempo de vida de la mascota: $horas h $minutos min\n" +
                            "💰 Monedas disponibles: ${monedas.value}"
                )
            }
        )
    }


    if (!enTutorial) {
        if (mostrarCambioEtapa) {
            AlertDialog(
                onDismissRequest = { mostrarCambioEtapa = false },
                confirmButton = {
                    TextButton(onClick = { mostrarCambioEtapa = false }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("🌱 Cambio de etapa") },
                text = {
                    Text(
                        "Tu planta ha crecido: ahora está en la etapa ${
                            etapaActual.name.lowercase().capitalize()
                        }!"
                    )
                }
            )
        }
    }
    if (mostrarMensajeInicio) {
        MostrarMensajeInicioPlanta {
            mostrarMensajeInicio = false
        }
    }
    if (mostrarSaludo) {
        // Leer valores actuales
        val riegosTotales = mascotaEstado.riegos
        val ciclos = mascotaEstado.ciclosCompletados

        // Logro por ciclos
        val logroCiclo = listaDeLogros.getOrElse(ciclos.coerceIn(0, listaDeLogros.size - 1)) { "Explorador de la tierra" }

        // Logro por puntos
        val puntosActuales = mascotaEstado.puntos // o cualquier variable que use para puntos
        val logroPuntos = when (puntosActuales) {
            in 0 until 500 -> "Gente normal"
            in 501 until 1000 -> "Agricultor"
            in 1001 until 1500 -> "Granjero"
            in 1501 until 2000 -> "Viverista"
            in 2001 until 2500 -> "Reciclador"
            in 2501 until 3000 -> "Cazador de especies"
            in 3001 until 3500 -> "Jardinero"
            in 3501 until 4000 -> "Paisajista"
            in 4001 until 4500 -> "Maestro de la pala"
            in 4501 until 5000 -> "Maestro Jardinero"
            in 5001 until 5500 -> "Granjero Valiente"
            in 5501 until 6000 -> "Cazador de insectos"
            in 6001 until 6500 -> "Agricultor de oro"
            in 6501 until 7000 -> "Granjero de Platino"
            in 7001 until 7500 -> "Gran agricultor"
            in 7501 until 8000 -> "Gran Maestro de las Plantas"
            else -> "Sabio Vegetal"
        }

        // Resumen
        val resumenIndicadores = """
             🏆 Logro por ciclos de cultivo: $logroCiclo
        🎖 Logro por cuidados: $logroPuntos
        
        💧 Humedad: ${mascotaEstado.agua}0%
        😊 Felicidad: ${mascotaEstado.feliz}0%
        🌱 Nutrientes: ${mascotaEstado.nutrientes}0%
        🐛 Plagas: ${mascotaEstado.plagas}0%
        🚿 Riegos totales: $riegosTotales
        🔄 Ciclos completados: $ciclos
        🎖  Puntos: ${mascotaEstado.puntos}
    """.trimIndent()

        AlertDialog(
            onDismissRequest = { mostrarSaludo = false },
            confirmButton = {
                TextButton(onClick = { mostrarSaludo = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("🌱 Bienvenido, ${nombreUsuario.value}") },
            text = { Text(resumenIndicadores) }
        )
    }



//reinicio manual o por muerte descuido
    if (mostrarDialogo.value) {
        Dialog(onDismissRequest = {}) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                var nombre by remember { mutableStateOf("") }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("¡Bienvenido! ¿Cómo te llamas?")
                    TextField(value = nombre, onValueChange = { nombre = it })
                    Spacer(Modifier.height(8.dp))
                    Text("¿Qué tipo de Biotamon quieres?")
                    DropdownMenuTipoBiotamon(
                        seleccion = tipoGeneralSeleccionado,
                        opciones = opcionesTipoBiotamon,
                        onSeleccionar = { tipoGeneralSeleccionado = it }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("¿Qué planta quieres cuidar?")
                    DropdownMenuPlantas(
                        seleccion = plantaSeleccionada,
                        opciones = opcionesTipoPlanta,
                        onSeleccionar = { plantaSeleccionada = it }
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        prefs?.limpiarDatos()
                        iniciarNuevaPartida(
                            prefs,
                            nombre,
                            tipoGeneralSeleccionado,
                            plantaSeleccionada
                        )
                        mostrarDialogo.value = false
                        enTutorial = true
                        pasoTutorial = 1
                        actualizarMonedas(0)
                        muerteProcesada = true

                        val nuevaMascota = prefs.cargarMascota()
                        mascotaEstado = nuevaMascota
                    }) {
                        Text("Comenzar")
                    }
                }
            }
        }
    }
    if (mascotaEstado.causaMuerte == CausaMuerte.DESCUIDO_SEQUIA
        || mascotaEstado.causaMuerte == CausaMuerte.DESCUIDO_NUTRIENTES
        || mascotaEstado.causaMuerte == CausaMuerte.DESCUIDO_PLAGAS
        || mascotaEstado.causaMuerte == CausaMuerte.DESCUIDO_EXCESO_AGUA) {

        val mensajeCausa = when (mascotaEstado.causaMuerte) {
            CausaMuerte.DESCUIDO_SEQUIA -> "☠️ Tu planta murió por falta de agua"
            CausaMuerte.DESCUIDO_NUTRIENTES -> "☠️ Tu planta murió por falta de nutrientes"
            CausaMuerte.DESCUIDO_PLAGAS -> "☠️ Tu planta murió a causa de plagas"
            CausaMuerte.DESCUIDO_EXCESO_AGUA -> "☠️ Tu planta murió por exceso de agua"
            else -> "☠️ Tu planta ha muerto"
        }
        // --- Mostrar diálogo resumen de muerte por descuido ---
        if (mostrarResumenMuerteDescuido) {
            AlertDialog(
                onDismissRequest = { mostrarResumenMuerteDescuido = false },
                title = { Text(mensajeCausa)},
                text = {
                    Text(
                        "Resumen de tu Juego, ${nombreUsuario.value}:\n\n" +
                                "🌼 Especie de última Mascota: ${tipoPlanta.value}\n" +
                                "🌿 Riegos totales en juego: ${mascotaEstado.riegos}\n" +
                                "🔁 Ciclos completados: ${mascotaEstado.ciclosCompletados}\n" +
                                "💰 Monedas alcanzadas: ${monedas.value}"
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        muerteProcesadaDescuido = true
                        mostrarResumenMuerteDescuido = false
                        mostrarDialogo.value = true // Abrimos el diálogo de nueva partida
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        mostrarResumenMuerteDescuido = false
                        muerteProcesadaDescuido = true
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    // Convertimos 40% de la altura total a valor en píxeles
                    val screenHeightPx = size.height.toFloat()
                    val zonaJugableInicioY = screenHeightPx * 0.23f
                    if (esDentroDeZonaJugable(tapOffset, zonaJugableInicioY)) {
                        if (enTutorial && pasoTutorial == 1) {
                            semillaPosicion = tapOffset
                            prefs.guardarPosicionSemilla(tapOffset)
                            pasoTutorial = 2
                        }
                        //si damos click dentro del area correcta dibujar ahi al personaje
                        semillaPosicion = tapOffset
                        objetivo = tapOffset
                        prefs.guardarPosicionSemilla(semillaPosicion!!)
                        prefs.guardarFloat("semilla_x", semillaPosicion!!.x)
                        prefs.guardarFloat("semilla_y", semillaPosicion!!.y)
                        //se guarda la posicion
                    }
                }
            }
    ) {
        // Fondo dinámico segun la hora un color distinto
        FondoDinamico(hora = horaActual.hour, modifier = Modifier.fillMaxSize())
        if (enTutorial) {
            val texto = when (pasoTutorial) {
                1 -> "🌱 Toca la pantalla para colocar la semilla"
                2 -> "💧 Toca el botón de riego para establecer tu planta"
                else -> ""
            }
            if (texto.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 8.dp
                    ) {
                        Text(
                            text = texto,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        // En el Box principal
        frutasEnPantalla.forEach { frutaEnPantalla ->
            Image(
                painter = painterResource(id = frutaEnPantalla.fruta.id),
                contentDescription = "Fruta lanzada",
                modifier = Modifier
                    .offset {
                        IntOffset(
                            frutaEnPantalla.posicion.x.toInt(),
                            frutaEnPantalla.posicion.y.toInt()
                        )
                    }
                    .size(frutaEnPantalla.fruta.tamano)
                    .pointerInput(frutaEnPantalla) {
                        detectDragGestures { _, dragAmount ->
                            val nuevaPos = frutaEnPantalla.posicion + dragAmount
                            frutaEnPantalla.posicion = nuevaPos

                            if (detectaColisionConPersonaje(nuevaPos, posicionPersonaje)) {
                                // Agrega a la lista de frutas a eliminar
                                frutasAEliminar.add(frutaEnPantalla)

                                // Actualizar los valores
                                val nuevosNutrientes = (prefs.obtenerInt("nutrientes") + frutaEnPantalla.fruta.valorNutriente).coerceAtMost(10)
                                val nuevaFelicidad = (prefs.obtenerInt("feliz") + frutaEnPantalla.fruta.valorFelicidad).coerceAtMost(10)

                                prefs.guardarIndicador("nutrientes", nuevosNutrientes)
                                prefs.guardarIndicador("feliz", nuevaFelicidad)
                                prefs.guardarLong("fechaUltimosNutrientes", System.currentTimeMillis())
// Cada vez que quieras subir puntos
                                val puntosNuevos = prefs.sumarPuntos(10) // +10 puntos, por ejemplo
                                mascotaEstado.puntos = puntosNuevos

                                mascotaEstado = mascotaEstado.copy(
                                    nutrientes = nuevosNutrientes,
                                    feliz = nuevaFelicidad,
                                    resistencia = prefs.obtenerInt("resistencia"),
                                    germinacion = prefs.obtenerInt("germinacion"),
                                    acuatica = prefs.obtenerInt("acuatica"),
                                    aerea = prefs.obtenerInt("aerea"),
                                    parasita = prefs.obtenerInt("parasita"),
                                    propagacion = prefs.obtenerInt("propagacion"),
                                    simbiosis = prefs.obtenerInt("simbiosis"),
                                    adaptacion = prefs.obtenerInt("adaptacion")
                                )
                                audioViewModel2.reproducirEfecto(R.raw.comer)
                            }
                        }
                    }
            )
        }

        premiosEnPantalla.forEach { premioEnPantalla ->
            if (animacionesActivas.contains(premioEnPantalla)) {
                DibujarAnimacionPremio(
                    premio = premioEnPantalla.premio,
                    posicion = premioEnPantalla.posicion,
                    repeticiones = 3,
                    onAnimacionCompleta = {
                        animacionesActivas.remove(premioEnPantalla)
                    }
                )
            } else {
                Image(
                    painter = painterResource(id = premioEnPantalla.premio.drawable),
                    contentDescription = "Premio colocado",
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                premioEnPantalla.posicion.x.toInt(),
                                premioEnPantalla.posicion.y.toInt()
                            )
                        }
                        .size(premioEnPantalla.premio.tamano)
                        .pointerInput(premioEnPantalla) {
                            detectDragGestures { _, dragAmount ->
                                val nuevaPos = premioEnPantalla.posicion + dragAmount
                                premioEnPantalla.posicion = nuevaPos

                                if (detectaColisionConRegresar(nuevaPos, posicionPersonaje)) {
                                    if (!animacionesActivas.contains(premioEnPantalla)) {
                                        animacionesActivas.add(premioEnPantalla)

                                        when (premioEnPantalla.premio.tipo) {
                                            TipoPremio.BATE -> {
                                                audioViewModel2.reproducirEfecto(R.raw.premio2)


                                            }


                                            TipoPremio.PELOTA -> {
                                                audioViewModel2.reproducirEfecto(R.raw.premio3)
                                                animacionPelotaActiva = premioEnPantalla
                                            }

                                            TipoPremio.SAXOFON -> {
                                                val musica = listOf(
                                                    R.raw.premio41,
                                                    R.raw.premio42,
                                                    R.raw.premio43,
                                                    R.raw.premio44
                                                ).random()
                                                audioViewModel2.reproducirEfecto(musica)
                                                saxofonActivo = premioEnPantalla
                                            }

                                            TipoPremio.TORTUGA -> {
                                                audioViewModel2.reproducirEfecto(R.raw.comer)
                                                tortugaActiva = premioEnPantalla
                                                direccionTortuga = listOf(-1, 1).random() // elige dirección al azar
                                                posicionTortuga = premioEnPantalla.posicion
                                                premiosAEliminar.add(premioEnPantalla)
                                            }


                                            TipoPremio.PAPALOTE -> {
                                                audioViewModel2.reproducirEfecto(R.raw.viento)
                                                papaloteActivo = premioEnPantalla // << lo usaremos luego para dibujarlo
                                                premiosAEliminar.add(premioEnPantalla) // elimina después de la animación
                                            }

                                            /*
                                            TipoPremio.HIERBA -> {
                                                mostrarAnimacionBrotes = false
                                                audioViewModel2.reproducirEfecto(R.raw.comer)
                                            }*/

                                            else -> {
                                                audioViewModel2.reproducirEfecto(premioEnPantalla.premio.sonido)
                                            }
                                        }
                                    }
                                }

                            }
                        }
                )
            }
        }



        val density = LocalDensity.current
        val configuration = LocalConfiguration.current
        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
// Detectar colisión entre pelota y bate
        // Revisar si se colisiona bate y pelota
        val bate = premiosEnPantalla.find { it.premio.tipo == TipoPremio.BATE }
        val pelota = premiosEnPantalla.find { it.premio.tipo == TipoPremio.PELOTA }

        if (
            bate != null && pelota != null &&
            animacionPelotaBateada == null &&
            detectaColisionEntrePremios(
                pelota.posicion,
                DpSize(pelota.premio.tamano, pelota.premio.tamano),  // ✅ Tamaño corregido
                bate.posicion,
                DpSize(bate.premio.tamano, bate.premio.tamano)        // ✅ Tamaño corregido
            )
        ) {
            val vi = (350..650).random().toFloat()
            val ai = (40..75).random().toFloat()
            val gr = (280..500).random().toFloat()

            val puntosTrayectoria = generarTrayectoriaSinRebotes(
                origen = bate.posicion,
                velocidadInicial = vi,
                anguloInicial = ai,
                gravedad = gr,
                widthPx = screenWidthPx,
                heightPx = screenHeightPx,
                duracionTotal = 5f,
                pasos = 120
            )

            puntosPelotaRebote = puntosTrayectoria
            animacionPelotaBateada = pelota
            audioViewModel2.reproducirEfecto(R.raw.premio3)
        }

// Animación cuando la pelota es bateada
        animacionPelotaBateada?.let { premio ->
            AnimarPelotaRebotando(
                imagenId = premio.premio.drawable,
                puntos = puntosPelotaRebote,
                tamano = premio.premio.tamano,
                onFin = {
                    animacionPelotaBateada = null
                    animacionesActivas.remove(pelota)
                }
            )
        }

        animacionPelotaActiva?.let { premioEnPantalla ->
            val puntos = remember {
                val vi = (350..650).random().toFloat()
                val ai = (40..75).random().toFloat()
                val gr = (280..500).random().toFloat()
                generarTrayectoriaSinRebotes(
                    origen = posicionPersonaje,
                    velocidadInicial = vi,
                    anguloInicial = ai,
                    gravedad = gr,
                    widthPx = screenWidthPx,
                    heightPx = screenHeightPx,
                    duracionTotal = 5f,
                    pasos = 120
                )
            }

            AnimarPelotaRebotando(
                imagenId = premioEnPantalla.premio.drawable,
                puntos = puntos,
                tamano = premioEnPantalla.premio.tamano,
                onFin = {
                    animacionPelotaActiva = null
                    animacionesActivas.remove(premioEnPantalla)
                }
            )
        }

        tortugaActiva?.let { tortuga ->




// Velocidades iniciales (puedes randomizarlas)
            var direccionTortugaX by remember { mutableStateOf(if ((0..1).random() == 0) -1 else 1) }
            var direccionTortugaY by remember { mutableStateOf(if ((0..1).random() == 0) -1 else 1) }

            val velocidad = 5f
            val frameList = animacionesPremios[if (direccionTortugaX == 1) 5 else 4] ?: listOf(tortuga.premio.drawable)

            var frameIndex by remember { mutableStateOf(0) }

            LaunchedEffect(tortuga) {
                val zonaJugableInicioY = screenHeightPx * 0.23f

                while (true) {
                    delay(100L)

                    frameIndex = (frameIndex + 1) % frameList.size

                    val nuevaX = posicionTortuga.x + (velocidad * direccionTortugaX)
                    val nuevaY = posicionTortuga.y + (velocidad * direccionTortugaY)
                    val tamanoPx = with(density) { tortuga.premio.tamano.toPx() }

                    // Rebotar horizontalmente
                    if (nuevaX in 0f..(screenWidthPx - tamanoPx)) {
                        posicionTortuga = Offset(nuevaX, posicionTortuga.y)
                    } else {
                        direccionTortugaX *= -1
                    }

                    // Rebotar verticalmente dentro del área jugable
                    if (nuevaY in zonaJugableInicioY..(screenHeightPx - tamanoPx)) {
                        posicionTortuga = Offset(posicionTortuga.x, nuevaY)
                    } else {
                        direccionTortugaY *= -1
                    }

                    // 🍎 Detectar colisión con frutas
                    val frutasParaEliminar = mutableListOf<FrutaEnPantalla>()
                    frutasEnPantalla.forEach { fruta ->
                        val dx = posicionTortuga.x - fruta.posicion.x
                        val dy = posicionTortuga.y - fruta.posicion.y
                        val distancia = sqrt(dx * dx + dy * dy)
                        val radioTortuga = tamanoPx / 2
                        val radioFruta = with(density) { fruta.fruta.tamano.toPx() / 2 }

                        if (distancia < radioTortuga + radioFruta) {
                            frutasParaEliminar.add(fruta)
                            audioViewModel2.reproducirEfecto(R.raw.comer)
                        }
                    }
                    frutasEnPantalla.removeAll(frutasParaEliminar)

                    // Cambio aleatorio de dirección
                    if ((0..500).random() < 5) direccionTortugaX *= -1
                    if ((0..500).random() < 5) direccionTortugaY *= -1
                }
            }



            // Dibujar la tortuga
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(posicionTortuga.x.toInt(), posicionTortuga.y.toInt())
                    }
                    .size(tortuga.premio.tamano)
            ) {
                Image(
                    painter = painterResource(id = frameList[frameIndex % frameList.size]),
                    contentDescription = "Tortuga caminando",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        papaloteActivo?.let { premio ->
            val puntos = remember {
                generarTrayectoriaPapalote(
                    origen = premio.posicion,
                    widthPx = screenWidthPx,
                    heightPx = screenHeightPx,
                    pasos = 100
                )
            }

            AnimarPapaloteZigZag(
                imagenId = premio.premio.drawable,
                puntos = puntos,
                tamano = premio.premio.tamano,
                onFin = {
                    papaloteActivo = null
                    animacionesActivas.remove(premio)
                }
            )
        }


        saxofonActivo?.let { premio ->
            AnimarSaxofonFantastico(
                premio = premio,
                onFin = {
                    saxofonActivo = null
                    animacionesActivas.remove(premio)
                }
            )
        }



        LaunchedEffect(premiosAEliminar.size) {
            premiosEnPantalla.removeAll(premiosAEliminar)
            premiosAEliminar.clear()
        }

// Fuera del forEach y dentro del mismo composable
        LaunchedEffect(frutasAEliminar.size) {
            frutasEnPantalla.removeAll(frutasAEliminar)
            frutasAEliminar.clear()
        }

//   Activar si hay muchas plagas
        LaunchedEffect(mascotaEstado.plagas) {
            mostrarAnimacionPlagas = mascotaEstado.plagas > 4
        }
        LaunchedEffect(mascotaEstado.nutrientes, brotesEnPausaHasta) {
            val ahora = System.currentTimeMillis()
            mostrarAnimacionBrotes =
                mascotaEstado.nutrientes > 3 && ahora > brotesEnPausaHasta
        }
        LaunchedEffect(Unit) {
            while (true) {
                delay(60000) //20 seg en ppantalla activa baja feliz
                if (mascotaEstado.feliz > 0) {
                    mascotaEstado = mascotaEstado.copy(
                        feliz = (mascotaEstado.feliz - 1).coerceIn(0, 10),
                        fechaUltimaFelicidad = System.currentTimeMillis()
                    )
                    prefs.guardarIndicador("feliz", mascotaEstado.feliz)
                    prefs.guardarLong("fechaUltimaFelicidad", System.currentTimeMillis())
                }
            }
        }
        LaunchedEffect(Unit) {
            while (true) {
                val ahora = System.currentTimeMillis()
                val delayTiempo = if (mostrarAnimacionBrotes)
                    delayIndicadorNutrientes / 2 // más rápido si hay brotes
                else
                    delayIndicadorNutrientes
                delay(delayTiempo)
                if (mascotaEstado.nutrientes > 0) {
                    mascotaEstado = mascotaEstado.copy(
                        nutrientes = (mascotaEstado.nutrientes - 1).coerceIn(0, 10)
                    )
                    prefs.guardarIndicador("nutrientes", mascotaEstado.nutrientes)
                    prefs.guardarLong("fechaUltimosNutrientes", ahora)
                }
            }
        }
        LaunchedEffect(Unit) {
            while (true) {
                delay(60000) // 🔁 sube 1 punto cada 10 segundos con app activa
                if (mascotaEstado.plagas < 10) {
                    val nuevaPlaga = (mascotaEstado.plagas + 1).coerceIn(0, 10)
                    mascotaEstado = mascotaEstado.copy(plagas = nuevaPlaga)
                    prefs.guardarIndicador("plagas", nuevaPlaga)
                    prefs.guardarLong("fechaUltimasPlagas", System.currentTimeMillis())
                    Log.d("DEBUG_PLAGAS", "Nueva plaga: $nuevaPlaga")
                }
            }
        }


   LaunchedEffect(mascotaEstado.agua) {
            estadoMascota = if (mascotaEstado.agua <= 4) "seco" else "normal"
            prefs.guardarEstado("estado", estadoMascota)
        }

        LaunchedEffect(Unit) {
            while (true) {
                delay(360000) // cada 6 min actualiza el nivel de agua pantalla activa
                val aguaActual = calcularNivelAgua(prefs)

                mascotaEstado = mascotaEstado.copy(
                    agua = aguaActual
                )
            }
        }



        // ✅ Esto se ejecuta una sola vez al inicio
        LaunchedEffect(Unit) {
            if (mascotaEstado.etapa == Etapa.MUERTA && !muerteProcesada ) {
                delay(12000L) // pequeño delay para que Compose termine de montar
                mostrarResumenMuerte = true
            }
        }
        LaunchedEffect(mascotaEstado.etapa) {
            if (mascotaEstado.etapa == Etapa.MUERTA && !muerteProcesada ) {
                delay(1000L)
                if (mascotaEstado.etapa == Etapa.MUERTA && !muerteProcesada) {
                    mostrarResumenMuerte = true
                }
            }
        }
        LaunchedEffect(Unit) {
            while (true) {
                delay(1 * 60 * 1000L)  // hacerlo mas largo considerando las etapas mas cortas
                if (!enTutorial) {
                    nuevaEtapa = mascotaBase.determinarEtapa(etapaActual)
                    if (nuevaEtapa != etapaActual) {
                        // Mostrar animación de cambio de etapa si no es SEMILLA o SEMBRAR
                        if (!(
                                    (etapaActual == Etapa.SEMBRAR && nuevaEtapa == Etapa.SEMILLA) ||
                                            (etapaActual == Etapa.MARCHITA && nuevaEtapa == Etapa.MUERTA) ||
                                            (etapaActual == Etapa.MUERTA && nuevaEtapa == Etapa.SEMBRAR) ||
                                            (etapaActual == Etapa.MUERTA && nuevaEtapa == Etapa.SEMILLA) ||
                                            (nuevaEtapa == Etapa.SEMBRAR) || (etapaActual == Etapa.SEMBRAR) ||
                                            (nuevaEtapa == Etapa.MUERTA) || (etapaActual == Etapa.MUERTA)
                                    )
                        ) {
                            mostrarCambioEtapa = true
                        }
                        if ((etapaActual == Etapa.SEMBRAR && nuevaEtapa == Etapa.MUERTA)
                            || (etapaActual == Etapa.SEMILLA && nuevaEtapa == Etapa.MUERTA)
                        ) {
                        } else {
                            etapaActual = nuevaEtapa
                            mascotaEstado = mascotaEstado.copy(etapa = nuevaEtapa)
                            prefs.guardarEtapa(nuevaEtapa)
                        }
                        if (nuevaEtapa == Etapa.MUERTA && !resumenMostrado && !muerteProcesada) {
                            mostrarResumenMuerte = true
                            resumenMostrado = true
                            muerteProcesada = true

                        }
                    } else {
                        if (nuevaEtapa == Etapa.MUERTA && !resumenMostrado && !muerteProcesada   ) {
                            mostrarResumenMuerte = true
                            resumenMostrado = true
                            muerteProcesada = true

                        }
                    }
                }
            }
        }
// Si ya tenemos una mascota viva o muerta, dibujarla siempre (aunque no estemos en tutorial)
        if (juegoIniciado || (!enTutorial || pasoTutorial >= 3)) {
            if (etapaActual == Etapa.SEMBRAR) {
                semillaPosicion?.let { DibujarAnimacionSembrar(it) }
                posicionPersonaje = semillaPosicion
            } else {
                posicionPersonaje?.let {
                    PersonajeMovil(
                        tipoGeneral = tipoBiotamon,
                        etapa = etapaActual,
                        especie = plantaSeleccionada.lowercase(),
                        estado = estadoMascota,
                        objetivo = objetivo,
                        onLlegada = { objetivo = null },
                        onActualizarPosicion = { nuevaPos ->
                            posicionPersonaje = nuevaPos
                            prefs.guardarPosicionSemilla(nuevaPos)
                        },
                        audioViewModel = audioViewModel2,
                        onFelicidadAumentada = {
                            // 💚 Felicidad +1
                            val nuevaFelicidad = (prefs.obtenerInt("feliz") + 1).coerceAtMost(10)
                            prefs.guardarIndicador("feliz", nuevaFelicidad)
                            prefs.guardarLong("fechaUltimaFelicidad", ahora)
                        }
                    )

                }
            }
        } else if (enTutorial && pasoTutorial <= 3) {

            posicionPersonaje = semillaPosicion
            semillaPosicion?.let { DibujarAnimacionSembrar(it) }
        }
//animacion mostrar riego
        if (mostrarAnimacionRiego) {
            DibujarAnimacionRiego(
                semillaPosicion = posicionPersonaje,
                onAnimacionCompleta = {
                    mostrarAnimacionRiego = false
                }
            )
        }
        if (mostrarAnimacionPlagas) {
            // Mascota simple con datos
            val mascotaPlanta =
                MascotaPlanta(mascotaEstado) // Wrapper con comportamientos de planta
            DibujarAnimacionPlagas(
                mascota = mascotaPlanta,
                semillaPosicion = posicionPersonaje,
                nivelPlagas = mascotaEstado.plagas,
                onAnimacionCompleta = { /* ... */ }
            )
        }
        if (mostrarAnimacionBrotes) {
            val mascotaPlanta = MascotaPlanta(mascotaEstado)
            DibujarAnimacionBrotes(
                mascota = mascotaPlanta,
                semillaPosicion = posicionPersonaje,
                nivelNutrientes = mascotaEstado.nutrientes,
                onAnimacionCompleta = { /* ... */ }
            )
        }
        IndicadoresSuperpuestos(
            feliz = mascotaEstado.feliz,
            nutrientes = mascotaEstado.nutrientes,
            plagas = mascotaEstado.plagas
        )
        // Reloj
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp),
            contentAlignment = Alignment.TopStart
        ) {
            RelojConControles(
                hora = hora12,
                audioViewModel = audioViewModel2,
                prefs = prefs,
                mostrarDialogo = mostrarDialogo,
                mostrarVolumen = mostrarVolumen
            )

            if (mostrarVolumen.value) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 35.dp, top = 5.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    ControlVolumenDrag(audioViewModel = audioViewModel2)

                }
            }


        }
// Barra lateral izquierda con iconos
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 130.dp) // separa del borde y del top
                .width(60.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.iconse),
                contentDescription = "Icono SE",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        mostrarPanel = true
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.iconherramientas),
                contentDescription = "Otro herramientas",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        mostrarPanelHerramientas = true // Otra acción
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.iconmonedas),
                contentDescription = "Otro icono",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        mostrarPanelPremios = true
                    }
            )
            Text(
                text = "x ${monedas.value}",
                color = Color(0xFFFFD700), // Amarillo tipo oro
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(1.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ppt1),
                contentDescription = "ir al minijuego ppt",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {

                        mostrarPantallaPPT = true

                    }
            )
            Image(
                painter = painterResource(id = R.drawable.iconjuego1),
                contentDescription = "Otro icono",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        mostrarPantallaMemorama = true
                    }
            )
            Image(
                painter = painterResource(R.drawable.marica),
                contentDescription = "Otro icono",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        mostraPantallaPacBug = true
                    }
            )
            // Aquí irán más iconos después
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.iconregresar),
                contentDescription = "Botón Regresar",
                modifier = Modifier
                    .size(50.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        val localOffset = layoutCoordinates.positionInRoot()
                        posicionBotonRegresar.value = localOffset
                    }
                    .clickable {
                        onRegresarClick()
                    }
            )

        }
    }
    if (mostrarPanel) {
        PanelAtributos(mascota = mascotaEstado, onCerrar = { mostrarPanel = false })
    }
    if (mostrarPanelHerramientas) {
        PanelHerramientas(
            nivelAgua = mascotaEstado.agua,
            estadoActual = estadoMascota,

            onAtomizarClick = {
                if (monedas.value > 0) {
                    val ahora = System.currentTimeMillis()
                    // 💚 Subir felicidad
                    val nuevaFelicidad = (prefs.obtenerInt("feliz") + 1).coerceAtMost(10)
                    prefs.guardarIndicador2("feliz", nuevaFelicidad)
                    prefs.guardarLong("fechaUltimaFelicidad", ahora)
                    // 🐛 Bajar plagas
                    val plagasActual = prefs.obtenerInt("plagas")
                    val plagasReducidas = 3
                    val nuevaPlaga = (plagasActual - plagasReducidas).coerceIn(0, 10)
                    val intervaloPlagas = 10 * 60 * 1000L
                    val nuevaFechaUltimaPlagas = ahora + (plagasReducidas * intervaloPlagas)
                    prefs.guardarIndicador2("plagas", nuevaPlaga)
                    prefs.guardarLong("fechaUltimasPlagas", nuevaFechaUltimaPlagas)
                    // 🛑 Pausa
                    val ciclosDePausa = 5
                    plagasEnPausaHasta = ahora + (ciclosDePausa * intervaloPlagas)
                    prefs.guardarLong("plagasEnPausaHasta", plagasEnPausaHasta)
                    // 🪙 Monedas
                    monedas.value -= 1
                    prefs.guardarMonedas(monedas.value)
                    actualizarMonedas(monedas.value)
                    val puntosNuevos = prefs.sumarPuntos(10) // +10 puntos, por ejemplo
                    mascotaEstado.puntos = puntosNuevos

                    audioViewModel2.reproducirEfecto(R.raw.spray)
                }
            },
            onPodaClick = {
                if (monedas.value > 0) {
                    val ahora = System.currentTimeMillis()
                    val nuevaFelicidad = (prefs.obtenerInt("feliz") + 1).coerceAtMost(10)
                    prefs.guardarIndicador("feliz", nuevaFelicidad)
                    prefs.guardarLong("fechaUltimaFelicidad", ahora)
                    val ciclosPausa = 5
                    val delayTiempo = 1 * 60 * 1000L
                    brotesEnPausaHasta = System.currentTimeMillis() + (ciclosPausa * delayTiempo)
                    prefs.guardarLong("brotesEnPausaHasta", brotesEnPausaHasta)
                    mostrarAnimacionBrotes = false
                    monedas.value -= 1
                    prefs.guardarMonedas(monedas.value)
                    actualizarMonedas(monedas.value)
                    val puntosNuevos = prefs.sumarPuntos(10) // +10 puntos, por ejemplo
                    mascotaEstado.puntos = puntosNuevos
                    audioViewModel2.reproducirEfecto(R.raw.cortetijeras1)
                } else {
                    // Mensaje de "no tienes monedas"
                }
            },
            onRegar = {

                registrarRiego()
                revisarExcesoAgua()

// Cada vez que quieras subir puntos
                val puntosNuevos = prefs.sumarPuntos(10) // +10 puntos, por ejemplo
                mascotaEstado.puntos = puntosNuevos


                mostrarAnimacionRiego = true
                enTutorial = false
                pasoTutorial = 3
                val ahora = System.currentTimeMillis()
                // 💚 Felicidad +1
                val nuevaFelicidad = (prefs.obtenerInt("feliz") + 1).coerceAtMost(10)
                prefs.guardarIndicador("feliz", nuevaFelicidad)
                prefs.guardarLong("fechaUltimaFelicidad", ahora)
                // 🌱 Nutrientes +1
                val nuevaNutriente = (prefs.obtenerInt("nutrientes") + 1).coerceAtMost(10)
                prefs.guardarIndicador("nutrientes", nuevaNutriente)
                prefs.guardarLong("fechaUltimosNutrientes", ahora)
                // 🐛 Plagas -1
               // val nuevaPlaga = (prefs.obtenerInt("plagas") - 1).coerceIn(0, 10)
                //prefs.guardarIndicador("plagas", nuevaPlaga)
                //prefs.guardarLong("fechaUltimasPlagas", ahora)
                // 🌊 Riego y Etapas
                audioViewModel2.reproducirEfecto(R.raw.agua1)
                when {
                    mascotaEstado.riegos == 0 -> {
                        // 🔰 Primer riego: inicio del juego
                        etapaActual = Etapa.SEMILLA
                        muerteProcesada = true
                        mostrarMensajeInicio = true
                        val nuevosRiegos = 1
                        estadoMascota = "normal"
                        prefs.guardarIndicador("riegos", nuevosRiegos)
                        prefs.guardarIndicador("agua", 10)
                        prefs.guardarLong("fechaUltimoRiego", ahora)
                        prefs.guardarEstado("estado", estadoMascota)
                        prefs.guardarEtapa(Etapa.SEMILLA) // <-- si tienes una función guardarEtapa
                        prefs.guardarJuegoIniciado(true)
                        mascotaEstado = mascotaEstado.copy(
                            riegos = nuevosRiegos,
                            agua = 10,
                            fechaUltimoRiego = ahora,
                            etapa = Etapa.SEMILLA,
                            estado = "normal"
                        )
                        monedas.value += 100
                        prefs.guardarMonedas(monedas.value)
                    }
                    mascotaEstado.etapa == Etapa.SEMBRAR -> {
                        // 🌱 Cambio de etapa
                        etapaActual = Etapa.SEMILLA
                        nuevaEtapa = Etapa.SEMILLA
                        muerteProcesada = true
                        estadoMascota = "normal"
                        val nuevosRiegos = mascotaEstado.riegos + 1
                        prefs.guardarIndicador("riegos", nuevosRiegos)
                        prefs.guardarIndicador("agua", 10)
                        prefs.guardarLong("fechaUltimoRiego", ahora)
                        prefs.guardarEstado("estado", estadoMascota)
                        prefs.guardarEtapa(Etapa.SEMILLA)
                        mascotaEstado = mascotaEstado.copy(
                            riegos = nuevosRiegos,
                            agua = 10,
                            fechaUltimoRiego = ahora,
                            etapa = Etapa.SEMILLA,
                            estado = "normal"
                        )
                        monedas.value += 100
                        prefs.guardarMonedas(monedas.value)
                    }
                    mascotaEstado.etapa == Etapa.MUERTA -> {
                        // ☠️ Solo cambia estado visual
                        muerteProcesada = false
                        mostrarResumenMuerte = true
                    }
                    else -> {
                        // 🌿 Riego normal
                        val nuevosRiegos = mascotaEstado.riegos + 1
                        estadoMascota = "normal"
                        prefs.guardarIndicador("riegos", nuevosRiegos)
                        prefs.guardarIndicador("agua", 10)
                        prefs.guardarLong("fechaUltimoRiego", ahora)
                        prefs.guardarEstado("estado", estadoMascota)
                        mascotaEstado = mascotaEstado.copy(
                            riegos = nuevosRiegos,
                            agua = 10,
                            fechaUltimoRiego = ahora,
                            estado = "normal"
                        )
                        monedas.value += 1000
                        prefs.guardarMonedas(monedas.value)
                    }
                }
            },
            onCerrar = { mostrarPanelHerramientas = false },
            onAbrirAlimentos = {
                mostrarPanelAlimentos = true
                mostrarPanelHerramientas = false
            },
            plagas = mascotaEstado.plagas,
            monedas = monedas,
            onRevisarPlagas = {
                mostrarPantallaInspeccion = true
            }
        )
    }
    if (mostrarPanelAlimentos) {
        PanelAlimentos(
            onCerrar = { mostrarPanelAlimentos = false },
            frutasEnPantalla = frutasEnPantalla,
            prefs = prefs,
            monedas = monedas
        )
    }
    if (mostrarPanelPremios) {
        PanelPremios( onCerrar = { mostrarPanelPremios = false },
            premiosEnPantalla = premiosEnPantalla,
            prefs = prefs,
            monedas = monedas)
    }
    if (mostrarPantallaInspeccion) {
        PantallaInspeccion(
            audioViewModel = audioViewModel2,
            onSalir = { mostrarPantallaInspeccion = false },
            prefs = prefs,
            monedas = monedas )
    }
    if (mostrarPantallaPPT) {
        PantallaPiedraPapelTijeras(
            audioViewModel = audioViewModel2,
            onSalir =   { mostrarPantallaPPT = false },
            prefs = prefs,
            monedas = monedas )
    }
    if (mostrarPantallaMemorama) {
        PantallaMemorama(
            audioViewModel = audioViewModel2,
            onSalir =   { mostrarPantallaMemorama = false },
            prefs = prefs,
            monedas = monedas )
    }
    if (mostraPantallaPacBug) {
        PantallaPacBug(
            audioViewModel = audioViewModel2,
            onSalir =   { mostraPantallaPacBug = false },
            prefs = prefs,
            monedas = monedas )
    }
}

@Composable
fun DropdownMenuPlantas(
    seleccion: String,
    opciones: List<String>,
    onSeleccionar: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(seleccion, modifier = Modifier.clickable { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSeleccionar(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuTipoBiotamon(
    seleccion: String,
    opciones: List<String>,
    onSeleccionar: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(seleccion)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ✅ Función reutilizable para verificar si un Offset está dentro de la zona jugable
fun esDentroDeZonaJugable(offset: Offset, zonaInicioY: Float): Boolean {
    return offset.y >= zonaInicioY
}
@Composable
fun MostrarMensajeInicioPlanta(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        },
        title = { Text("🌱 Planta establecida") },
        text = { Text("¡Has establecido tu planta!\n juega, riega, revisa y fertiliza. :) ") }
    )
}
@Composable
fun MostrarDialogoCambioBiotamon(
    tipoActual: String,
    plantaActual: String,
    opcionesTipoBiotamon: List<String>,
    opcionesTipoPlanta: List<String>,
    onConfirmar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var tipoSeleccionado by remember { mutableStateOf(tipoActual) }
    var plantaSeleccionada by remember { mutableStateOf(plantaActual) }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("🌿 Elegir nuevo Biotamon") },
        text = {
            Column {
                Text("¿Qué tipo de Biotamon quieres?")
                DropdownMenuTipoBiotamon(
                    seleccion = tipoSeleccionado,
                    opciones = opcionesTipoBiotamon,
                    onSeleccionar = { tipoSeleccionado = it }
                )
                DropdownMenuPlantas(
                    seleccion = plantaSeleccionada,
                    opciones = opcionesTipoPlanta,
                    onSeleccionar = { plantaSeleccionada = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmar(tipoSeleccionado, plantaSeleccionada)
            }) {
                Text("🌱 Cambiar y resembrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun PersonajeMovil(
    tipoGeneral: Int,
    etapa: Etapa,
    especie: String,
    estado: String,
    objetivo: Offset?,
    onLlegada: () -> Unit,
    onActualizarPosicion: (Offset) -> Unit,
    audioViewModel: GameAudioViewModel2, // nuevo parámetro
    onFelicidadAumentada: () -> Unit     // para que tú controles eso como ya tienes
) {
    var posicion by remember { mutableStateOf(Offset(450f, 1000f)) }
    var mostrarBurbuja by remember { mutableStateOf(false) }
    var imagenBurbujaId by remember { mutableStateOf(R.drawable.xx1) }

    val listaSonidos = listOf(
        R.raw.p1,
        R.raw.p2,
        R.raw.p3,
        R.raw.p4,
        R.raw.p5,
        R.raw.p6,
        R.raw.p7,
        R.raw.p8,
        R.raw.p9,
        R.raw.p10
    )

    val listaBurbujas = listOf(
        R.drawable.xx1,
        R.drawable.xx2,
        R.drawable.xx3,
        R.drawable.xx4,
        R.drawable.xx5,
        R.drawable.xx6,
        R.drawable.xx7,
        R.drawable.xx8,
        R.drawable.xx9,
        R.drawable.xx10,
        R.drawable.xx11,
        R.drawable.xx12,
        R.drawable.xx13,
        R.drawable.xx14,
        R.drawable.xx15,
        R.drawable.xx16,
        R.drawable.xx17,
        R.drawable.xx18,
        R.drawable.xx19
    )

    // Movimiento del personaje
    LaunchedEffect(objetivo) {
        while (true) {
            delay(60L)

            if (objetivo != null) {
                val dx = objetivo.x - posicion.x
                val dy = objetivo.y - posicion.y

                val paso = 0.05f
                val nuevaX = posicion.x + dx * paso
                val nuevaY = posicion.y + dy * paso

                posicion = Offset(
                    nuevaX.coerceIn(100f, 900f),
                    nuevaY.coerceIn(300f, 1600f)
                )
                onActualizarPosicion(posicion)

                val distancia = dx * dx + dy * dy
                if (distancia < 100f) {
                    onLlegada()
                }
            } else {
                val dx = (-2..2).random().toFloat()
                val dy = (-2..2).random().toFloat()

                val nueva = posicion + Offset(dx, dy)

                posicion = Offset(
                    nueva.x.coerceIn(100f, 900f),
                    nueva.y.coerceIn(300f, 1600f)
                )
                onActualizarPosicion(posicion)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(150.dp)
            .offset {
                IntOffset(
                    x = (posicion.x - 50).toInt(),
                    y = (posicion.y - 100).toInt()
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // sonido aleatorio
                        val sonidoAleatorio = listaSonidos.random()
                        audioViewModel.reproducirEfecto(sonidoAleatorio)

                        // burbuja aleatoria
                        imagenBurbujaId = listaBurbujas.random()
                        mostrarBurbuja = true
                        onFelicidadAumentada()

                        // ocultar burbuja después de 2 segundos
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            mostrarBurbuja = false
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        DibujarPersonajeAnimacionLoop(
            tipoGeneral = tipoGeneral,
            etapa = etapa,
            especie = especie,
            estado = estado,
            semillaPosicion = Offset(50f, 100f) // ← posicion relativa al Box
        )

        if (mostrarBurbuja) {
            Image(
                painter = painterResource(id = imagenBurbujaId),
                contentDescription = "Burbuja de diálogo",
                modifier = Modifier
                    .size(150.dp)
                    .offset(y = (-80).dp) // sube un poco
            )
        }
    }
}

