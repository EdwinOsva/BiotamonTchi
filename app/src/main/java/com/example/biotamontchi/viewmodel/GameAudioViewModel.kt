package com.example.biotamontchi.viewmodel

// GameAudioViewModel.kt

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biotamontchi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameAudioViewModel(application: Application) : AndroidViewModel(application) {
    private var backgroundPlayer: MediaPlayer? = null
    private var clickPlayer: MediaPlayer? = null

    private val context = application.applicationContext

    fun playClickSound() {
        viewModelScope.launch(Dispatchers.IO) {
            clickPlayer?.release()
            clickPlayer = MediaPlayer.create(context, R.raw.clic2)
            clickPlayer?.start()
        }
    }

    fun startBackgroundMusic() {
        if (backgroundPlayer == null) {
            backgroundPlayer = MediaPlayer.create(context, R.raw.viento1)
            backgroundPlayer?.isLooping = true
            backgroundPlayer?.start()
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

    override fun onCleared() {
        super.onCleared()
        clickPlayer?.release()
        backgroundPlayer?.release()
    }
}
