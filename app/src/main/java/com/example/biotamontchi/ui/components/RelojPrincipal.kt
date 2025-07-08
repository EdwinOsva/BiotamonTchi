package com.example.biotamontchi.ui.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import com.example.biotamontchi.data.PrefsManager
import com.example.biotamontchi.viewmodel.GameAudioViewModel

@Composable
fun MostrarImagenReloj(hora: Int, modifier: Modifier = Modifier) {
    val imageId = when (hora) {
        1 -> R.drawable.reloj1
        2 -> R.drawable.reloj2
        3 -> R.drawable.reloj3
        4 -> R.drawable.reloj4
        5 -> R.drawable.reloj5
        6 -> R.drawable.reloj6
        7 -> R.drawable.reloj7
        8 -> R.drawable.reloj8
        9 -> R.drawable.reloj9
        10 -> R.drawable.reloj10
        11 -> R.drawable.reloj11
        12 -> R.drawable.reloj12
        else -> R.drawable.reloj12
    }

    Image(
        painter = painterResource(id = imageId),
        contentDescription = "Reloj",
        modifier = modifier,  // ← Usa solo este
        contentScale = ContentScale.Fit
    )
}

@Composable
fun RelojConControles(
    hora: Int,
    audioViewModel: GameAudioViewModel? = null,
    prefs: PrefsManager? = null,
    mostrarDialogo: MutableState<Boolean>? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.TopStart
    ) {
        MostrarImagenReloj(
            hora = hora,
            modifier = Modifier
                .size(100.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            audioViewModel?.let {
                                if (it.estaReproduciendoMusica()) {
                                    it.stopBackgroundMusic()
                                } else {
                                    it.startBackgroundMusic()
                                }
                            }
                        },
                        onLongPress = {
                            prefs?.limpiarDatos()
                            mostrarDialogo?.value = true
                        }
                    )
                }
        )
    }
}
