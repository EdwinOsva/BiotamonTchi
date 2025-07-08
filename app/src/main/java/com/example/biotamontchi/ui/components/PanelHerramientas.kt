package com.example.biotamontchi.ui.components

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.biotamontchi.data.Mascota
import com.example.biotamontchi.data.MascotaBase
import com.example.biotamontchi.data.PrefsManager
import kotlinx.coroutines.delay


@Composable
fun PanelHerramientas(
    mascota: Mascota,


    monedas: MutableState<Int>,

    onActualizarMonedas: (Int) -> Unit,
    onPodaClick: () -> Unit,
    onRegar: () -> Unit,
    onCerrar: () -> Unit,
    onAbrirAlimentos: () -> Unit
)
{
    var mostrarPanelAlimentos by remember { mutableStateOf(false) }


    var context = LocalContext.current
    var prefs = remember { PrefsManager(context) }

    val fechaUltimoRiego = prefs.obtenerLong("fechaUltimoRiego")
    val ahora = System.currentTimeMillis()
    val intervalo = 1 * 60 * 1000L // 20 minutos en milisegundospara indicador del agua pantalla activa
    val tiempoTranscurrido = ahora - fechaUltimoRiego
    val porcentaje = tiempoTranscurrido.toFloat() / intervalo.toFloat()

    var nivelAgua = when {
        porcentaje >= 1f -> 0 // Ya pasó   el tiempo, sin agua
        porcentaje < 0f -> 10 // Seguridad por si algo está mal
        else -> 10 - (porcentaje * 10).toInt()
    }

    var mascotaEstado by remember { mutableStateOf(prefs.cargarMascota()) }
    val nombreRecurso by derivedStateOf { "agua$nivelAgua" }
    val aguaIconoId by derivedStateOf {
        context.resources.getIdentifier(nombreRecurso, "drawable", context.packageName)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // cada minuto nivel del agua
            nivelAgua = calcularNivelAgua(prefs)
            mascotaEstado.agua = nivelAgua
            prefs.guardarMascota(mascota)
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

                                   val totalRiegos = mascotaEstado.riegos + 1

                                    // Actualizamos la mascota
                                    mascotaEstado = mascotaEstado.copy(
                                        agua = 10,
                                        riegos = totalRiegos,
                                        fechaUltimoRiego = ahora
                                    )

                                    // Guardamos en preferencias
                                    prefs.guardarMascota(mascotaEstado)

                                    // Monedas
                                    monedas.value += 10
                                    prefs.guardarMonedas(monedas.value)

                                    onActualizarMonedas(monedas.value)


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
                        Image(
                            painter = painterResource(id = R.drawable.bichos2),
                            contentDescription = "Botón inspección",
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    // Acción para regresar
                               //     revisar()
                                }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.atomizar),
                            contentDescription = "Botón insecticida",
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    // Acción para regresar
                                   // atomizar()
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
        PanelAlimentos( mascota = mascotaEstado, onCerrar = { mostrarPanelAlimentos = false })
    }
}
