package com.example.biotamontchi.model

// GameAudioViewModelFactory.kt
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biotamontchi.viewmodel.GameAudioViewModel

class GameAudioViewModelFactory(
    private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameAudioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameAudioViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
