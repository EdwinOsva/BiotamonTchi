package com.example.biotamontchi.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.biotamontchi.data.FrutaEnPantalla
import com.example.biotamontchi.data.PrefsManager
import kotlinx.coroutines.delay


@Composable
fun PanelHerramientas(
    nivelAgua: Int,
    estadoActual: String,

    onAtomizarClick: () -> Unit,
    onPodaClick: () -> Unit,
    onRegar: () -> Unit,
    onCerrar: () -> Unit,
    onAbrirAlimentos: () -> Unit,
    plagas:Int,
    monedas: MutableState<Int>,
    onRevisarPlagas: () -> Unit
)
{
    /*
    var mostrarPanelAlimentos by remember { mutableStateOf(false) }

    val frutasEnPantalla = remember { mutableStateListOf<FrutaEnPantalla>() }

    val context = LocalContext.current
    val prefs = remember { PrefsManager(context) }

    val fechaUltimoRiego = prefs.obtenerLong("fechaUltimoRiego")
    val ahora = System.currentTimeMillis()
    val intervalo = 1 * 60 * 60 * 1000L // 1 hora del agua pantalla activa
    val tiempoTranscurrido = ahora - fechaUltimoRiego
    val porcentaje = tiempoTranscurrido.toFloat() / intervalo.toFloat()

    var nivelAgua = when {
        porcentaje >= 1f -> 0 // Ya pasó   el tiempo, sin agua
        porcentaje < 0f -> 10 // Seguridad por si algo está mal
        else -> 10 - (porcentaje * 10).toInt()
    }

    var estadoActual by remember { mutableStateOf(if (nivelAgua <= 4) "seco" else "normal") }

    val nombreRecurso by derivedStateOf { "agua$nivelAgua" } //aqui pasamos el primer valor al abrie el panel
    val aguaIconoId by derivedStateOf {
        context.resources.getIdentifier(nombreRecurso, "drawable", context.packageName)
    }



    LaunchedEffect(Unit) {
        while (true) {
            delay(30000) // cada 30 min con panel abierto, se calcula el segundo valor de agua,
            nivelAgua = obtenerNivelAgua(prefs)
            estadoActual = if (nivelAgua <= 4) "seco" else "normal"

            prefs.guardarAgua(nivelAgua)
            prefs.guardarEstado("estado", estadoActual)
        }
    }

*/


    var mostrarPanelAlimentos by remember { mutableStateOf(false) }
    val frutasEnPantalla = remember { mutableStateListOf<FrutaEnPantalla>() }

    val context = LocalContext.current
    val prefs = remember { PrefsManager(context) }
    val nombreRecurso by remember(nivelAgua) { derivedStateOf { "agua$nivelAgua" } }
    val aguaIconoId by remember(nombreRecurso) {
        derivedStateOf {
            context.resources.getIdentifier(nombreRecurso, "drawable", context.packageName)
        }
    }









    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x7C05D8BF))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            // Botón regresar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onCerrar) {
                    Icon(
                        painter = painterResource(id = R.drawable.iconregresar),
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }

            // Tarjeta
            Card(
                modifier = Modifier

                    .fillMaxWidth()
                    .fillMaxHeight(0.6f),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x65C4F7EB) // <--- Color con transparencia personalizado
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fila 1: 2 imágenes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Image(
                            painter = painterResource(id = aguaIconoId),
                            contentDescription = "Nivel de Agua",
                            modifier = Modifier.size(150.dp)
                        )

                        Image(
                            painter = painterResource(id = R.drawable.iconriego),
                            contentDescription = "Botón Regar",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {

                                    onRegar()





                                    onCerrar()
                                }
                        )



                    }

                    // Fila 2: 2 imágenes + $1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconoPlagas = if (plagas < 4) R.drawable.bichos1 else R.drawable.bichos2

                        Image(
                            painter = painterResource(id = iconoPlagas),
                            contentDescription = "Botón inspección",
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    // Acción para entrar al minijuego o revisar
                                    if (plagas >= 4) {
                                        onRevisarPlagas()
                                        }
                                    }

                        )

                        Image(
                            painter = painterResource(id = R.drawable.atomizar),
                            contentDescription = "Botón insecticida",
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    // Acción para regresar
                                    onAtomizarClick()
                                    onCerrar()
                                }
                        )
                        Text(
                            text = "$1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    // Fila 3: 3 imágenes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.iconfrutas),
                            contentDescription = "Botón alimento",
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    // Acción para regresar
                                    onAbrirAlimentos()
                                }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.poda),
                            contentDescription = "Botón Podar",
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    onPodaClick()
                                    onCerrar()
                                }
                        )

                        Text(
                            text = "$1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                    }
                }
            }
        }
    }


    if (mostrarPanelAlimentos) {
        PanelAlimentos(
            onCerrar = { mostrarPanelAlimentos = false },
            frutasEnPantalla = frutasEnPantalla,
            prefs = prefs,
            monedas = monedas
        )
    }
}

