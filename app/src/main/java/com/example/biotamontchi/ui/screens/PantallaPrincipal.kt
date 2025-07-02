package com.example.biotamontchi.ui.screens

import android.content.Context
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
import com.example.biotamontchi.ui.components.MostrarImagenReloj
import com.example.biotamontchi.viewmodel.VistaHoraReloj
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import com.example.biotamontchi.data.Etapa
import com.example.biotamontchi.data.Mascota
import com.example.biotamontchi.data.determinarEtapa
import com.example.biotamontchi.ui.components.DibujarAnimacionMaduraLoop
import com.example.biotamontchi.ui.components.DibujarAnimacionMarchitaLoop
import com.example.biotamontchi.ui.components.DibujarAnimacionMuertaLoop
import com.example.biotamontchi.ui.components.DibujarAnimacionPlantaLoop
import com.example.biotamontchi.ui.components.DibujarAnimacionPlantulaLoop
import com.example.biotamontchi.ui.components.DibujarAnimacionRiego
import com.example.biotamontchi.ui.components.DibujarAnimacionSembrar
import com.example.biotamontchi.ui.components.DibujarAnimacionSemillaLoop
import com.example.biotamontchi.ui.components.PanelAlimentos
import com.example.biotamontchi.ui.components.PanelAtributos
import com.example.biotamontchi.ui.components.PanelHerramientas
import com.example.biotamontchi.ui.components.PanelPremios
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(viewModelHora: VistaHoraReloj, onRegresarClick: () -> Unit) {

    val horaActual by viewModelHora.estadoHora.collectAsState()
    // Context y preferencias
    val context = LocalContext.current
    val prefs = remember { PrefsManager(context) }
    // Nombre de usuario y tipo de planta
    val nombreUsuario = remember { mutableStateOf(prefs.obtenerNombreUsuario()) }
    val tipoPlanta = remember { mutableStateOf(prefs.obtenerTipoPlanta()) }
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
    //comienza la logica para mostrar los datos guardados en los indicadores

    var enTutorial by remember { mutableStateOf(false) }
    var pasoTutorial by remember { mutableStateOf(0) }

    val intervalo = 20 * 60 * 1000L // 20 minutos en milisegundos



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

// Estado de la posición de la semilla
    var semillaPosicion by remember { mutableStateOf<Offset?>(centroOffset) }

    //cargar mascota
    var mascotaCargada = prefs.cargarMascota()
    var mascotaEstado by remember { mutableStateOf(mascotaCargada) }





    val x = prefs.obtenerFloat("semilla_x")
    val y = prefs.obtenerFloat("semilla_y")
    semillaPosicion = if (x != null && y != null) Offset(x, y) else null

    val hora12 = if (horaActual.hour % 12 == 0) 12 else horaActual.hour % 12



    var tiempoTranscurrido by remember { mutableStateOf(0L) }

    val porcentaje = tiempoTranscurrido.toFloat() / intervalo.toFloat()
    val nivelAgua = when {
        porcentaje >= 1f -> 0 // Ya pasó   el tiempo, sin agua
        porcentaje < 0f -> 10 // Seguridad por si algo está mal
        else -> 10 - (porcentaje * 10).toInt()
    }

    val minutosRestantes =  nivelAgua * 2



    var mostrarMensajeInicio by remember { mutableStateOf(false) }
    var mostrarDialogoReinicio by remember { mutableStateOf(false) }

    val etapaInicial = remember {
        if (mascotaEstado.riegos == 0) {
            Etapa.SEMBRAR
        } else{

                determinarEtapa(System.currentTimeMillis() - mascotaEstado.fechaInicioJuego)

        }
    }
    var etapaActual by remember { mutableStateOf(etapaInicial) }

    var mostrarCambioEtapa by remember { mutableStateOf(false) }

    var etapaMaxima by remember { mutableStateOf(Etapa.SEMBRAR) }


    fun cambiarEtapaManual(nuevaEtapa: Etapa) {
        if (nuevaEtapa.ordinal >= etapaMaxima.ordinal) {
            etapaMaxima = nuevaEtapa
            etapaActual = nuevaEtapa
        } else {
            // Opcional: permitir bajar etapa manualmente si quieres, pero
            // entonces la lógica automática podría sobreescribirla luego.
        }
    }


    // Función para actualizar monedas y guardar en preferencias
    fun actualizarMonedas(nuevaCantidad: Int) {
        monedas.value = nuevaCantidad
        val editor = context.getSharedPreferences("datosMascota", Context.MODE_PRIVATE).edit()
        editor.putInt("monedas", nuevaCantidad)
        editor.apply()
    }

    fun iniciarNuevaPartida(prefs: PrefsManager, nombre: String, tipo: String) {
        val ahora = System.currentTimeMillis()
        val indicadoresIniciales = listOf(
            "agua", "felíz", "nutrientes", "plagas",
            "resistencia", "germinación", "acuática", "aérea",
            "parásita", "propagación", "simbiósis", "adaptación"
        )
        // Guardar nombre y tipo de planta
        prefs.guardarNombreUsuario(nombre)
        prefs.guardarTipoPlanta(tipo)

        // Reiniciar indicadores a 0
        indicadoresIniciales.forEach {
            prefs.guardarIndicador(it, 0)
        }

        val nuevaMascota = Mascota(
            agua = 0,
            feliz = 0,
            nutrientes = 0,
            plagas = 0,
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
            etapaMaxima = Etapa.SEMBRAR // inicializamos etapa máxima aquí
        )
        val centroOffset = with(density) {
            Offset(
                x = screenWidth.toPx() / 2,
                y = screenHeight.toPx() / 2
            )
        }
        val posicionPorDefecto = centroOffset
        prefs.guardarPosicionSemilla(posicionPorDefecto)

        enTutorial = true
        pasoTutorial = 0
        tiempoTranscurrido = 0L

        prefs.guardarMascota(nuevaMascota)
        actualizarMonedas(0)
        prefs.guardarMonedas(0)

        // Ya no necesitas llamar determinarEtapa aquí directamente porque la lógica
        // de actualización automática va a leer la etapa real y respetar la etapaMaxima

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

    if (mostrarDialogoReinicio) {
        MostrarDialogoReinicioPlanta(
            onConfirmar = {
                mostrarDialogoReinicio = false
                mascotaEstado = mascotaEstado.copy(
                    ciclosCompletados = mascotaEstado.ciclosCompletados + 1,
                    fechaInicioJuego = System.currentTimeMillis(),
                    fechaUltimoRiego = 0L,

                    etapa = Etapa.SEMBRAR
                    // no cambias lo demás, los demás atributos quedan igual
                )
                prefs.guardarMascota(mascotaEstado)
            },
            onCancelar = {
                mostrarDialogoReinicio = false
            }
        )
    }


    if (mostrarMensajeInicio) {
        MostrarMensajeInicioPlanta {
            mostrarMensajeInicio = false
        }
    }


    if (mostrarSaludo) {
        AlertDialog(
            onDismissRequest = { mostrarSaludo = false },
            confirmButton = {
                TextButton(onClick = { mostrarSaludo = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("🌱 Bienvenido, ${nombreUsuario.value}") },
            text = {
                Text("Humedad: $nivelAgua\nTe quedan ~$minutosRestantes min antes de que se seque tu planta.")
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
                var plantaSeleccionada by remember { mutableStateOf("Margarita") }


                Column(modifier = Modifier.padding(16.dp)) {
                    Text("¡Bienvenido! ¿Cómo te llamas?")
                    TextField(value = nombre, onValueChange = { nombre = it })

                    Spacer(Modifier.height(8.dp))
                    Text("¿Qué planta quieres cuidar?")
                    DropdownMenuPlantas(
                        seleccion = plantaSeleccionada,
                        onSeleccionar = { plantaSeleccionada = it }
                    )

                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        iniciarNuevaPartida(prefs, nombre, plantaSeleccionada)

                        mostrarDialogo.value = false
                        enTutorial = true
                        pasoTutorial = 1


                        actualizarMonedas(0)

                        mascotaCargada = prefs.cargarMascota()
                        mascotaEstado = mascotaCargada
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
                detectTapGestures {  tapOffset ->
                    // Convertimos 40% de la altura total a valor en píxeles
                    val screenHeightPx = size.height.toFloat()
                    val zonaJugableInicioY = screenHeightPx * 0.23f
                    if (esDentroDeZonaJugable( tapOffset, zonaJugableInicioY)) {

                        if (enTutorial && pasoTutorial == 1) {
                            semillaPosicion = tapOffset
                            prefs.guardarPosicionSemilla(tapOffset)
                            pasoTutorial = 2
                        }

                        //si damos click dentro del area correcta dibujar ahi al personaje
                        semillaPosicion =  tapOffset
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

        LaunchedEffect(Unit) {
            while (true) {
                delay(3* 1000L) // cada 3 seg

                if (!enTutorial) { // solo si no estamos en tutorial
                    val nuevaEtapa = determinarEtapa(System.currentTimeMillis() - mascotaEstado.fechaInicioJuego)

                    if (nuevaEtapa != etapaActual) {
                        if (!(
                                    (etapaActual == Etapa.SEMBRAR && nuevaEtapa == Etapa.SEMILLA) ||
                                            (etapaActual == Etapa.MUERTA && nuevaEtapa == Etapa.SEMBRAR) ||
                                            (nuevaEtapa == Etapa.SEMBRAR)
                                    )) {
                            mostrarCambioEtapa = true
                        }

                        etapaActual = nuevaEtapa
                    }
                }
            }
        }




        if (semillaPosicion != null && (!enTutorial || pasoTutorial >=3)) {
            PersonajeUno(etapaActual, semillaPosicion)
        } else if (enTutorial && pasoTutorial <= 3 ) {
            // Mostrar solo una guía visual para indicar dónde va la semilla
            semillaPosicion?.let { DibujarAnimacionSembrar(it) }
          /*  Image(
                painter = painterResource(id = R.drawable.semilla1),
                contentDescription = "Toca para colocar la semilla",
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (screenWidth ).roundToPx(),
                            (screenHeight ).roundToPx()
                        )
                    }
                    .size(48.dp)
            )*/

        }

      //  PersonajeUno(etapaActual, semillaPosicion)

//animacion mostrar riego
        if (mostrarAnimacionRiego && semillaPosicion != null ) {

            DibujarAnimacionRiego(
                semillaPosicion = semillaPosicion!!,
                onAnimacionCompleta = {
                    mostrarAnimacionRiego = false
                }
            )
        }

        // Reloj
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            MostrarImagenReloj(
                hora = hora12,
                modifier = Modifier
                    .size(100.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                prefs.limpiarDatos()
                                mostrarDialogo.value = true
                            }
                        )
                    }
            )
        }
// Barra lateral izquierda con iconos
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 18.dp, top = 150.dp) // separa del borde y del top
                .width(72.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp)
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
            mascota = mascotaEstado,

            monedas = monedas,  // pasa MutableState<Int> entero, no monedas.value

            onActualizarMonedas = { nuevaCantidad -> actualizarMonedas(nuevaCantidad) },
            onCerrar = { mostrarPanelHerramientas = false },
            onRegar = {
                mostrarAnimacionRiego = true
                enTutorial = false
                pasoTutorial = 3

                val ahora = System.currentTimeMillis()

                when {
                    mascotaEstado.riegos == 0 -> {
                        // 🔰 Primer riego
                        mascotaEstado.fechaUltimoRiego = ahora
                        mascotaEstado.riegos = 1
                        etapaActual = Etapa.SEMILLA
                        // Guardar y sumar monedas
                        prefs.guardarMascota(mascotaEstado)

                        mostrarMensajeInicio = true
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
                monedas.value += 1
            },


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
fun DropdownMenuPlantas(seleccion: String, onSeleccionar: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Margarita", "Amapola", "Lili")

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

// ✅ Función reutilizable para verificar si un Offset está dentro de la zona jugable
fun esDentroDeZonaJugable(offset: Offset, zonaInicioY: Float): Boolean {
    return offset.y >= zonaInicioY
}

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
                    posicion = posicion // 👈 ¡aquí está la clave!
        )
    }

}

fun calcularNivelAgua(prefs: PrefsManager, intervalo: Long = 20 * 60 * 1000): Int {
    val fechaUltimoRiego = prefs.obtenerLong("fechaUltimoRiego")
    val ahora = System.currentTimeMillis()
    val transcurrido = ahora - fechaUltimoRiego
    val porcentaje = transcurrido.toFloat() / intervalo.toFloat()

    return when {
        porcentaje >= 1f -> 0
        porcentaje < 0f -> 10
        else -> 10 - (porcentaje * 10).toInt()
    }

}
@Composable
fun PersonajeUno(
    etapa: Etapa,
    semillaPosicion: Offset?
) {
    if (semillaPosicion == null) return



    when (etapa) {
        Etapa.SEMBRAR -> DibujarAnimacionSembrar(semillaPosicion)
        Etapa.SEMILLA -> DibujarAnimacionSemillaLoop(semillaPosicion)
        Etapa.PLANTULA -> DibujarAnimacionPlantulaLoop(semillaPosicion)
        Etapa.PLANTA -> DibujarAnimacionPlantaLoop(semillaPosicion)
        Etapa.MADURA -> DibujarAnimacionMaduraLoop(semillaPosicion)
        Etapa.MARCHITA -> DibujarAnimacionMarchitaLoop(semillaPosicion)
        Etapa.MUERTA -> DibujarAnimacionMuertaLoop(semillaPosicion)
    }
}
@Composable
fun MostrarDialogoReinicioPlanta(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("🌱 Resembrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        },
        title = { Text("☠️ Tu planta ha muerto") },
        text = { Text("¿Quieres sembrar una nueva semilla y comenzar otro ciclo?") }
    )
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
        text = { Text("¡Has establecido tu planta!\nTu siguiente riego será en 20 minutos.") }
    )
}
