package com.example.biotamontchi.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime




class VistaHoraReloj : ViewModel() {

    val estadoHora = MutableStateFlow(LocalTime.now())

    init {
        viewModelScope.launch {
            while (true) {
                delay(60_000L) // Actualiza cada minuto
                estadoHora.value = LocalTime.now()
            }
        }
    }


}