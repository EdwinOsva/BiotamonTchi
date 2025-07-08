package com.example.biotamontchi.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import com.example.biotamontchi.ui.components.FondoDinamico
import com.example.biotamontchi.ui.components.MostrarImagenReloj
import com.example.biotamontchi.ui.components.RelojConControles
import com.example.biotamontchi.viewmodel.GameAudioViewModel
import com.example.biotamontchi.viewmodel.VistaHoraReloj
import kotlinx.coroutines.delay
import kotlin.random.Random


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MenuInicial(
    onStartClick: () -> Unit,
    onExitClick: () -> Unit,
    viewmodel: VistaHoraReloj,
    audioViewModel: GameAudioViewModel
) {
    var startPressed by remember { mutableStateOf(false) }
    var exitPressed by remember { mutableStateOf(false) }

    val horaActual by viewmodel.estadoHora.collectAsState()

    val hora12 = if (horaActual.hour % 12 == 0) 12 else horaActual.hour % 12

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        FondoDinamico(hora = horaActual.hour, modifier = Modifier.fillMaxSize())
        Image(
            painter = painterResource(id = R.drawable.tama1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // 🕒 Imagen del reloj en la esquina superior izquierda
        Box(
            modifier = Modifier

                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            RelojConControles(
                hora = hora12,
                audioViewModel = audioViewModel
            )

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            // Animación del título con 4 imágenes
            val imagenesTitulo = listOf(
                R.drawable.titulo1,
                R.drawable.titulo2,
                R.drawable.titulo3,
                R.drawable.titulo4
            )

            var currentImageIndex by remember { mutableStateOf(0) }


// Cambiar imagen cada 500 ms
            LaunchedEffect(Unit) {
                while (true) {
                    delay(500)
                    currentImageIndex = Random.nextInt(imagenesTitulo.size)

                    // currentImageIndex = (currentImageIndex + 1) % imagenesTitulo.size
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
// Mostrar la imagen del título animada
            Image(
                painter = painterResource(id = imagenesTitulo[currentImageIndex]),
                contentDescription = "Título animado",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )


            Spacer(modifier = Modifier.height(80.dp))

            // Botón Iniciar (con cambio de imagen al presionar)
            Image(
                painter = painterResource(
                    id = if (startPressed) R.drawable.botoninicio2 else R.drawable.botoninicio1
                ),
                contentDescription = "Iniciar",
                modifier = Modifier
                    .width(200.dp)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                startPressed = true
                                audioViewModel.playClickSound()
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                startPressed = false
                                onStartClick()
                                true
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                startPressed = false
                                true
                            }
                            else -> false
                        }
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Salir (con cambio de imagen al presionar)
            Image(
                painter = painterResource(
                    id = if (exitPressed) R.drawable.botonsalir2 else R.drawable.botonsalir1
                ),
                contentDescription = "Salir",
                modifier = Modifier
                    .width(200.dp)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                exitPressed = true
                                audioViewModel.playClickSound()
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                exitPressed = false
                                onExitClick()
                                true
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                exitPressed = false
                                true
                            }
                            else -> false
                        }
                    }
            )
            // Animación para firma (firma1 a firma8)
            val imagenesFirma = listOf(
                R.drawable.firma1,
                R.drawable.firma2,
                R.drawable.firma3,
                R.drawable.firma4,
                R.drawable.firma5,
                R.drawable.firma6,
                R.drawable.firma7,
                R.drawable.firma8
            )

            var currentFirmaIndex by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(200L) // Velocidad del cambio de imagen
                    currentFirmaIndex = (currentFirmaIndex + 1) % imagenesFirma.size
                }
            }

// Firma animada en la parte inferior centrada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Creado por Edwin Arroyo, 2025.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Image(
                        painter = painterResource(id = imagenesFirma[currentFirmaIndex]),
                        contentDescription = "Firma animada",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

        }
    }
}
