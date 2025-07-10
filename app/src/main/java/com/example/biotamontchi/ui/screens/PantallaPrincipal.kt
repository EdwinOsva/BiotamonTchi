package com.example.biotamontchi.ui.screens

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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

import com.example.biotamontchi.viewmodel.GameAudioViewModel

import com.example.biotamontchi.data.MascotaAnimal
import com.example.biotamontchi.data.MascotaPlanta
import com.example.biotamontchi.data.PrefsManager
import com.example.biotamontchi.model.GameAudioViewModelFactory
import com.example.biotamontchi.ui.components.DibujarAnimacionPlagas
import com.example.biotamontchi.ui.components.RelojConControles

import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(viewModelHora: VistaHoraReloj, onRegresarClick: () -> Unit) {
    // Context y preferencias

    val context2 = LocalContext.current.applicationContext as Application
    val audioViewModel: GameAudioViewModel = viewModel(
        factory = GameAudioViewModelFactory(context2)
    )

    val horaActual by viewModelHora.estadoHora.collectAsState()
    val context = LocalContext.current
    val prefs = remember { PrefsManager(context) }
    // Nombre de usuario y tipo de planta
    val nombreUsuario = remember { mutableStateOf(prefs.obtenerNombreUsuario()) }
    var tipoPlanta = remember { mutableStateOf(prefs.obtenerTipoPlanta()) }
    //cargar mascota

    val mascotaGuardada = prefs.cargarMascota()

    var mascotaEstado by remember { mutableStateOf(mascotaGuardada) }

    val especieGuardada = prefs.cargarEspecie() ?: "margarita"
    var plantaSeleccionada by remember {
        mutableStateOf(mascotaGuardada.especie.replaceFirstChar { it.uppercase() })
    }

    val mostrarDialogo = remember { mutableStateOf(nombreUsuario.value == null || tipoPlanta.value == null) }
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

    val fechaUltimoRiego = mascotaEstado.fechaUltimoRiego
    val ahora = System.currentTimeMillis()
    val tiempoTranscurridoIndicador = ahora - fechaUltimoRiego
    val intervalo = 1 * 60 * 1000L // 20 minutos en milisegundos
    val porcentaje = tiempoTranscurridoIndicador.toFloat() / intervalo.toFloat()
    val nivelAgua = when {
        porcentaje >= 1f -> 0
        porcentaje < 0f -> 10
        else -> 10 - (porcentaje * 10).toInt()
    }
    val minutosRestantes = (nivelAgua * (intervalo / 10) / 60_000).toInt()
    var tiempoTranscurrido by remember { mutableStateOf(0L) }


    val nivelAguaInicial = calcularNivelAgua(prefs)

    mascotaEstado = mascotaEstado.copy(
        agua = nivelAguaInicial
    )





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

    var mostrarDialogoReinicio by remember { mutableStateOf(false) }
    var reintentarReinicio by remember { mutableStateOf(false) } // <- bandera para reintentar
    var  mostrarDialogoCambioBiotamon by remember { mutableStateOf(false) }

    val opcionesTipoBiotamon = listOf("Planta", "Animal", "Roca")
    val opcionesTipoPlanta = listOf("Margarita", "Amapola", "Lili")

    var objetivo by remember { mutableStateOf<Offset?>(null) }

    var estadoMascota by remember { mutableStateOf("normal") }
//indicadores
// NUTRIENTES

    var brotesEnPausaHasta by remember { mutableStateOf(0L) }


    val valorNutrientes = prefs.obtenerIndicador("nutrientes")
    val fechaUltimosNutrientes = prefs.obtenerLong("fechaUltimosNutrientes")

    Log.d("DEBUG_NUTRIENTES", "Nutrientes guardados: $valorNutrientes")
    Log.d("DEBUG_NUTRIENTES", "Última fecha: $fechaUltimosNutrientes")
    Log.d("DEBUG_NUTRIENTES", "Ahora: $ahora")
    Log.d("DEBUG_NUTRIENTES", "Transcurrido: ${ahora - fechaUltimosNutrientes}")

    val nutrientesCalculados = calcularNivelDesdeUltimoValor(
        valorGuardado = valorNutrientes,
        fechaUltima = fechaUltimosNutrientes,
        ahora = ahora,
        intervalo = 1000 * 60 * 10L // 🔁 cada 10 minutos baja 1 punto si la app está cerrada
    )

    Log.d("DEBUG_NUTRIENTES", "Nutrientes final: $nutrientesCalculados")

    mascotaEstado = mascotaEstado.copy(
        nutrientes = nutrientesCalculados
    )

    //feliz

    val valorFeliz = prefs.obtenerIndicador("feliz") // o mascota.feliz
    val fechaUltimaFelicidad = prefs.obtenerLong("fechaUltimaFelicidad")

    Log.d("DEBUG_FELIZ", "Feliz guardado: $valorFeliz")
    Log.d("DEBUG_FELIZ", "Última fecha: $fechaUltimaFelicidad")
    Log.d("DEBUG_FELIZ", "Ahora: $ahora")
    Log.d("DEBUG_FELIZ", "Transcurrido: ${ahora - fechaUltimaFelicidad}")

    val felicidadCalculada = calcularFelicidadDesdeUltimoValor(
        valorGuardado = valorFeliz,
        fechaUltima = fechaUltimaFelicidad,
        ahora = ahora
    )

    Log.d("DEBUG_FELIZ", "Feliz final: $felicidadCalculada")

    mascotaEstado = mascotaEstado.copy(
        feliz = felicidadCalculada
    )
    //plagas
    val valorPlagas = prefs.obtenerIndicador("plagas")
    val fechaUltimasPlagas = prefs.obtenerLong("fechaUltimasPlagas")


    val plagasCalculadas = calcularNivelDesdeUltimoValor2(
        valorGuardado = valorPlagas,
        fechaUltima = fechaUltimasPlagas,
        ahora = ahora,
        intervalo = 1000 * 60 * 10L // 🔁 cada 10 minutos sube 1 punto si la app está cerrada
    )


    mascotaEstado = mascotaEstado.copy(
        plagas = plagasCalculadas
    )
//animaciones plagas y brotes


    var mostrarAnimacionPlagas by remember { mutableStateOf(false) }

    var mostrarAnimacionBrotes by remember { mutableStateOf(false) }
    val delayIndicadorNutrientes = 30_000L

    var muerteProcesada by remember { mutableStateOf(false) }


    //estadisticas


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
            especie = planta.lowercase()
        )
        mostrarAnimacionPlagas =  false
        mostrarAnimacionBrotes = false
        mascotaEstado.plagas=0
        mascotaEstado.feliz=0
        mascotaEstado.nutrientes=0
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

        posicionPersonaje=centroOffset
        semillaPosicion=centroOffset
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
        mostrarDialogoReinicio = false
        mostrarResumenMuerte = false


        mascotaEstado = nuevaMascota

    }


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
                mostrarDialogoReinicio = false
                mostrarResumenMuerte = false
            },
            onCancelar = {
                mostrarDialogoCambioBiotamon = false
                muerteProcesada = false
            }
        )
    }

    if (mostrarResumenMuerte) {
        val tiempoVidaMs = mascotaEstado.tiempoVida.takeIf { it > 0 }
            ?: (System.currentTimeMillis() - mascotaEstado.fechaInicioJuego)

        val minutosVida = (tiempoVidaMs / 1000 / 60).toInt()
        val horas = minutosVida / 60
        val minutos = minutosVida % 60

        AlertDialog(
            onDismissRequest = {
                mostrarResumenMuerte = false
                reintentarReinicio = true
            },
            confirmButton = {
                TextButton(onClick = {
                    // ✅ Reiniciar con misma especie
                    mostrarResumenMuerte = false
                    mostrarDialogoCambioBiotamon = false
                    reintentarReinicio = false
                    muerteProcesada = true

                    mascotaEstado = mascotaEstado.copy(
                        ciclosCompletados = mascotaEstado.ciclosCompletados + 1,
                        fechaInicioJuego = System.currentTimeMillis(),
                        etapa = Etapa.SEMBRAR
                    )
                    etapaActual = Etapa.SEMBRAR
                    nuevaEtapa = Etapa.SEMBRAR
                    prefs.guardarMascota(mascotaEstado)
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
                        reintentarReinicio = true
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
        val mascotaBase = when (tipoBiotamon) {
            1 -> MascotaPlanta(mascotaEstado)
            2 -> MascotaAnimal(mascotaEstado)
            else -> MascotaPlanta(mascotaEstado) // fallback o MascotaRoca, etc.
        }

        val mensajeTipo = when (mascotaBase.tipo) {
            "planta" -> "tu ${especieGuardada} tiene una humedad de $nivelAgua%"
            "animal" -> "tu mascota ${especieGuardada} está con energía $nivelAgua%"
            else -> "tu compañero está con nivel $nivelAgua%"
        }

        AlertDialog(
            onDismissRequest = { mostrarSaludo = false },
            confirmButton = {
                TextButton(onClick = { mostrarSaludo = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("🌱 Bienvenido, ${nombreUsuario.value}") },
            text = {
                Text("🌱 $mensajeTipo\nTe quedan ~$minutosRestantes min antes de que ya no necesite atención y muera.")
            }
        )
    }

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
                        iniciarNuevaPartida(prefs, nombre, tipoGeneralSeleccionado, plantaSeleccionada)

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



//   Activar si hay muchas plagas
        LaunchedEffect(mascotaEstado.plagas) {
            mostrarAnimacionPlagas = mascotaEstado.plagas > 2
        }

        LaunchedEffect(mascotaEstado.nutrientes, brotesEnPausaHasta) {
            val ahora = System.currentTimeMillis()
            mostrarAnimacionBrotes =
                mascotaEstado.nutrientes > 3 && ahora > brotesEnPausaHasta
        }



        LaunchedEffect(Unit) {
            while (true) {
                delay(20000) //20 seg en ppantalla activa baja feliz

                if (mascotaEstado.feliz > 0) {
                    mascotaEstado = mascotaEstado.copy(
                        feliz = (mascotaEstado.feliz - 1).coerceIn(0, 10),
                        fechaUltimaFelicidad = System.currentTimeMillis()
                    )

                    prefs.guardarMascota(mascotaEstado)
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
                    prefs.guardarMascota(mascotaEstado)
                    prefs.guardarIndicador("nutrientes", mascotaEstado.nutrientes)
                    prefs.guardarLong("fechaUltimosNutrientes", ahora)
                }
            }
        }




        LaunchedEffect(Unit) {
            while (true) {
                delay(10000) // 🔁 sube 1 punto cada 10 segundos con app activa

                if (mascotaEstado.plagas < 10) {
                    val nuevaPlaga = (mascotaEstado.plagas + 1).coerceIn(0, 10)
                    mascotaEstado = mascotaEstado.copy(plagas = nuevaPlaga)

                    prefs.guardarMascota(mascotaEstado)
                    prefs.guardarIndicador("plagas", nuevaPlaga)
                    prefs.guardarLong("fechaUltimasPlagas", System.currentTimeMillis())

                    Log.d("DEBUG_PLAGAS", "Nueva plaga: $nuevaPlaga")
                }

            }
        }
        LaunchedEffect(mascotaEstado.agua) {
            estadoMascota = if (mascotaEstado.agua <= 4) "seco" else "normal"
        }
        LaunchedEffect(Unit) {
            while (true) {
                delay(5000) // cada 5 segundos actualiza el nivel de agua
                val aguaActual = calcularNivelAgua(prefs)

                mascotaEstado = mascotaEstado.copy(
                    agua = aguaActual
                )
            }
        }


        // ✅ Esto se ejecuta una sola vez al inicio
        LaunchedEffect(Unit) {
            if (mascotaEstado.etapa == Etapa.MUERTA && !muerteProcesada) {
                delay(2000L) // pequeño delay para que Compose termine de montar
                mostrarResumenMuerte = true
            }
        }
        LaunchedEffect(reintentarReinicio) {
            if (reintentarReinicio) {
                delay(60000L)
                if (mascotaEstado.etapa == Etapa.MUERTA && !muerteProcesada) {
                    mostrarResumenMuerte = true
                }
                reintentarReinicio = false
            }
        }



        LaunchedEffect(mascotaEstado.etapa) {
            if (mascotaEstado.etapa == Etapa.MUERTA && !muerteProcesada) {
                delay(1000L)
                if (mascotaEstado.etapa == Etapa.MUERTA && !muerteProcesada) {
                    mostrarResumenMuerte = true
                }
            }
        }

/*
        LaunchedEffect(Unit) {
            while (true) {
                delay(8 * 1000L)  // hacerlo mas largo considerando las etapas mas cortas

                if (!enTutorial) {
                     nuevaEtapa = mascotaBase.determinarEtapa(etapaActual)


                    if (nuevaEtapa != etapaActual) {
                        // Mostrar animación de cambio de etapa si no es SEMILLA o SEMBRAR
                        if (!(
                                    (etapaActual == Etapa.SEMBRAR && nuevaEtapa == Etapa.SEMILLA) ||

                                            (etapaActual == Etapa.MARCHITA && nuevaEtapa == Etapa.MUERTA) ||
                                            (etapaActual == Etapa.MUERTA && nuevaEtapa == Etapa.SEMBRAR) ||
                                            (nuevaEtapa == Etapa.SEMBRAR) || (etapaActual == Etapa.SEMBRAR)
                                    )
                        ) {
                            mostrarCambioEtapa = true
                        }

                        etapaActual = nuevaEtapa
                        mascotaEstado = mascotaEstado.copy(etapa = nuevaEtapa)
                        prefs.guardarMascota(mascotaEstado)

                        if (nuevaEtapa == Etapa.MUERTA && !resumenMostrado && !muerteProcesada) {
                            mostrarResumenMuerte = true
                            resumenMostrado = true
                            muerteProcesada = true
                        }
                    }
                }
            }
        }
*/
        LaunchedEffect(mascotaEstado) {
            while (true) {
                delay(8 * 1000L)

                if (!enTutorial) {
                    val nuevaMascotaBase = when (mascotaEstado.tipoBiotamon) {
                        1 -> MascotaPlanta(mascotaEstado)
                        2 -> MascotaAnimal(mascotaEstado)
                        else -> MascotaPlanta(mascotaEstado)
                    }

                    val etapaCalculada = nuevaMascotaBase.determinarEtapa(etapaActual)
                    nuevaEtapa = etapaCalculada

                    if (etapaCalculada != etapaActual) {
                        if (!(
                                    (etapaActual == Etapa.SEMBRAR && etapaCalculada == Etapa.SEMILLA) ||
                                            (etapaActual == Etapa.MARCHITA && etapaCalculada == Etapa.MUERTA) ||
                                            (etapaActual == Etapa.MUERTA && etapaCalculada == Etapa.SEMBRAR) ||
                                            (etapaCalculada == Etapa.SEMBRAR) || (etapaActual == Etapa.SEMBRAR)
                                    )
                        ) {
                            mostrarCambioEtapa = true
                        }

                        etapaActual = etapaCalculada
                        mascotaEstado = mascotaEstado.copy(etapa = etapaCalculada)
                        prefs.guardarMascota(mascotaEstado)

                        if (etapaCalculada == Etapa.MUERTA && !resumenMostrado && !muerteProcesada) {
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
            } else {
                semillaPosicion?.let {
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
                        }
                    )
                }
            }
        } else if (enTutorial && pasoTutorial <= 3) {
            semillaPosicion?.let { DibujarAnimacionSembrar(it) }
        }




//animacion mostrar riego
        if (mostrarAnimacionRiego ) {

            DibujarAnimacionRiego(
                semillaPosicion = posicionPersonaje,
                onAnimacionCompleta = {
                    mostrarAnimacionRiego = false
                }
            )
        }


        if (mostrarAnimacionPlagas) {

            // Mascota simple con datos
            val mascotaPlanta = MascotaPlanta(mascotaEstado) // Wrapper con comportamientos de planta


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
                .padding(8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            RelojConControles(
                hora = hora12,
                audioViewModel = audioViewModel,
                prefs = prefs,
                mostrarDialogo = mostrarDialogo
            )

        }


// Barra lateral izquierda con iconos
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 18.dp, top = 150.dp) // separa del borde y del top
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
                contentDescription = "Otro icono",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        // Otra acción
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.iconjuego1),
                contentDescription = "Otro icono",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        // Otra acción
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.iconjuego2),
                contentDescription = "Otro icono",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        // Otra acción
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
                    .clickable {
                        // Acción para regresar
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
            mascota  = mascotaEstado,
            monedas = monedas,  // pasa MutableState<Int> entero, no monedas.value
            onActualizarMonedas = { nuevaCantidad -> actualizarMonedas(nuevaCantidad) },
            onPodaClick = {
                val ciclosPausa = 5
                val delayTiempo = 30_000L // ciclo base de reducción de nutrientes
                brotesEnPausaHasta = System.currentTimeMillis() + (ciclosPausa * delayTiempo)
                mostrarAnimacionBrotes = false
                monedas.value -= 1
                prefs.guardarMonedas(monedas.value)
                actualizarMonedas(monedas.value)
            },

            onRegar = {
                mostrarAnimacionRiego = true
                enTutorial = false
                pasoTutorial = 3

                val ahora = System.currentTimeMillis()
//feliz
                Log.d("DEBUG_FELIZ", "anterior: ${mascotaEstado.feliz}")
                val nuevaFelicidad = (mascotaEstado.feliz + 1).coerceAtMost(10)

                mascotaEstado = mascotaEstado.copy(
                    feliz = nuevaFelicidad,
                    fechaUltimaFelicidad = ahora
                )

                prefs.guardarMascota(mascotaEstado)
                prefs.guardarIndicador("feliz", nuevaFelicidad) // ⬅️ asegúrate de guardar
                prefs.guardarLong("fechaUltimaFelicidad", ahora)
                Log.d("DEBUG_FELIZ", "Feliz guardado: $nuevaFelicidad")
                Log.d("DEBUG_FELIZ", "Última fecha: $ahora")
                Log.d("DEBUG_FELIZ", "Ahora: $ahora")
//nutriente
                val nuevaNutriente = (mascotaEstado.nutrientes + 1).coerceAtMost(10)

                mascotaEstado = mascotaEstado.copy(
                    nutrientes = nuevaNutriente,
                    fechaUltimosNutrientes = ahora
                )

                prefs.guardarMascota(mascotaEstado)
                prefs.guardarIndicador("nutrientes", nuevaNutriente)
                prefs.guardarLong("fechaUltimosNutrientes", ahora)

                Log.d("DEBUG_NUTRIENTES", "Nutrientes guardados: $nuevaNutriente")
//plaga
                val nuevaPlaga = (mascotaEstado.plagas - 2).coerceIn(0, 10)
                mascotaEstado = mascotaEstado.copy(
                    plagas = nuevaPlaga,
                    fechaUltimasPlagas = ahora
                )

                prefs.guardarMascota(mascotaEstado)
                prefs.guardarIndicador("plagas", nuevaPlaga)
                prefs.guardarLong("fechaUltimasPlagas", ahora)


                when {
                    mascotaEstado.riegos == 0 -> {
                        // 🔰 Primer riego
                        mascotaEstado.fechaUltimoRiego = ahora
                        mascotaEstado.riegos = 1
                        etapaActual = Etapa.SEMILLA
                        // Guardar y sumar monedas
                        prefs.guardarMascota(mascotaEstado)
                        prefs.guardarJuegoIniciado(true)
                        muerteProcesada=true
                        mostrarMensajeInicio = true
                    }
                    mascotaEstado.etapa == Etapa.SEMBRAR -> {
                        mascotaEstado = mascotaEstado.copy(
                            riegos =  + 1,
                            fechaUltimoRiego = ahora,
                            etapa = Etapa.SEMILLA
                        )
                        etapaActual = Etapa.SEMILLA

                        muerteProcesada=true
                        prefs.guardarMascota(mascotaEstado)
                    }
                    mascotaEstado.etapa == Etapa.MUERTA -> {
                        // ☠️ Planta muerta: ¿desea resembrar?
                        mostrarDialogoReinicio = true
                    }

                    else -> {
                        // 🌿 Riego normal
                        mascotaEstado.fechaUltimoRiego = ahora
                        mascotaEstado.riegos += 1


                    }
                }

                // Guardar y sumar monedas
                prefs.guardarMascota(mascotaEstado)

            },

            onCerrar = { mostrarPanelHerramientas = false },
            onAbrirAlimentos = {
                mostrarPanelAlimentos = true
                mostrarPanelHerramientas = false
            }
        )
    }
    if (mostrarPanelAlimentos) {
        PanelAlimentos(
            mascota = mascotaEstado,
            onCerrar = { mostrarPanelAlimentos = false }
        )
    }
    if (mostrarPanelPremios) {
        PanelPremios( mascota = mascotaEstado, onCerrar = { mostrarPanelPremios = false })
    }

}




@Composable
fun DropdownMenuPlantas(  seleccion: String,
                          opciones: List<String>,
                          onSeleccionar: (String) -> Unit) {
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
    onActualizarPosicion: (Offset) -> Unit
) {
    var posicion by remember { mutableStateOf(Offset(500f, 800f)) }

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
                onActualizarPosicion(posicion) // ✅ actualizamos fuera

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
                onActualizarPosicion(posicion) // ✅ actualizamos también aquí
            }
        }
    }

    DibujarPersonajeAnimacionLoop(
        tipoGeneral = tipoGeneral,
        etapa = etapa,
        especie = especie,
        estado = estado,
        semillaPosicion = posicion
    )
}


object UmbralesVida {
    const val SEMILLA =  15 * 1000L   // 1 minutos
    const val PLANTULA =   10 * 60 * 1000L    // 2 minutos
    const val PLANTA =   15 * 60 * 1000L      // 3 minutos
    const val MADURA =   25 * 60 * 1000L     // 4 minutos
    const val MARCHITA =  40 * 60 * 1000L     // 5 minutos
    const val MUERTA =   45 * 60 * 1000L    // 6 minutos
}
fun determinarEtapa(tiempoDesdeInicio: Long, etapaActual: Etapa = Etapa.SEMBRAR): Etapa {


    return when {


        tiempoDesdeInicio < UmbralesVida.SEMILLA -> Etapa.SEMBRAR
        tiempoDesdeInicio < UmbralesVida.PLANTULA -> Etapa.SEMILLA
        tiempoDesdeInicio < UmbralesVida.PLANTA -> Etapa.PLANTULA
        tiempoDesdeInicio < UmbralesVida.MADURA -> Etapa.PLANTA
        tiempoDesdeInicio < UmbralesVida.MARCHITA -> Etapa.MADURA
        tiempoDesdeInicio < UmbralesVida.MUERTA -> Etapa.MARCHITA
        else -> Etapa.MUERTA
    }
}