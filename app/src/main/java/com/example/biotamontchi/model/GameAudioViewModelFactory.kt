package com.example.biotamontchi.model

// GameAudioViewModelFactory.kt
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import com.example.biotamontchi.R
import com.example.biotamontchi.data.PrefsManager

import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameAudioViewModel2(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var efectoPlayer: MediaPlayer? = null
    private val prefs = PrefsManager(context)
    private var backgroundPlayer: MediaPlayer? = null
    private var clickPlayer: MediaPlayer? = null
    private var volumen: Float = prefs.obtenerVolumen() // 0.0 a 1.0

    fun startBackgroundMusic(resId: Int = R.raw.ambiente3) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer?.let {
            it.isLooping = true
            it.setVolume(volumen, volumen)
            it.start()
        }
    }



    fun reproducirEfecto(resId: Int) {
        if (!prefs.sonidoActivado()) return

        efectoPlayer?.release() // Libera anterior
        efectoPlayer = MediaPlayer.create(context, resId).apply {
            setVolume(volumen, volumen)
            start()
            setOnCompletionListener { release() }
        }
    }
    fun playClickSound() {
        viewModelScope.launch(Dispatchers.IO) {
            clickPlayer?.release()
            clickPlayer = MediaPlayer.create(context, R.raw.clic1)
            clickPlayer?.start()
        }
    }



    fun stopBackgroundMusic() {
        backgroundPlayer?.stop()
        backgroundPlayer?.release()
        backgroundPlayer = null
    }

    fun estaReproduciendoMusica(): Boolean {
        return backgroundPlayer?.isPlaying == true
    }


    fun cambiarVolumen(nuevoVolumen: Float) {
        volumen = nuevoVolumen.coerceIn(0f, 1f)
        prefs.guardarVolumen(volumen)
        mediaPlayer?.setVolume(volumen, volumen)
    }

    fun obtenerVolumen(): Float = volumen

    fun silenciarTodo(silenciar: Boolean) {
        prefs.guardarSonidoActivado(!silenciar)
        if (silenciar) {
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(volumen, volumen)
        }
    }

    override fun onCleared() {
        mediaPlayer?.release()
        efectoPlayer?.release()
        super.onCleared()
    }
}

class GameAudioViewModelFactory(
    private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameAudioViewModel2::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameAudioViewModel2(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun ControlVolumenDrag(audioViewModel: GameAudioViewModel2) {
    val volumen = audioViewModel.obtenerVolumen()
    val nivel = (volumen * 10).toInt().coerceIn(0, 10)
    val imagenId = when (nivel) {
        0 -> R.drawable.se00
        1 -> R.drawable.se01
        2 -> R.drawable.se02
        3 -> R.drawable.se03
        4 -> R.drawable.se04
        5 -> R.drawable.se05
        6 -> R.drawable.se06
        7 -> R.drawable.se07
        8 -> R.drawable.se08
        9 -> R.drawable.se09
        10 -> R.drawable.se10
        else -> R.drawable.se05
    }

    Box(
        modifier = Modifier
            .padding(start = 16.dp, top = 110.dp) // justo abajo del reloj
            .size(100.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val y = change.position.y
                    val nuevoNivel = (10 - (y / 10)).toInt().coerceIn(0, 10)
                    val nuevoVol = nuevoNivel / 10f
                    audioViewModel.cambiarVolumen(nuevoVol)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "Volumen: $nivel",
            modifier = Modifier
                .fillMaxSize()
                .rotate(270f) // ← aquí está el giro
        )
    }
}

