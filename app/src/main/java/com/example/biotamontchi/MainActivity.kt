package com.example.biotamontchi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.example.biotamontchi.ui.screens.MenuInicial
import com.example.biotamontchi.ui.theme.BiotamonTchiTheme
import com.example.biotamontchi.viewmodel.VistaHoraReloj

import com.example.biotamontchi.viewmodel.GameAudioViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biotamontchi.ui.screens.PantallaPrincipal
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BiotamonTchiTheme {
                // A surface container using the 'background' color from the theme
                val relojViewModel: VistaHoraReloj = viewModel()
                val audioViewModel: GameAudioViewModel = viewModel()


                var mostrarPantallaJuego by rememberSaveable { mutableStateOf(false) }
                if (mostrarPantallaJuego) {
                    PantallaPrincipal(
                        viewModelHora = relojViewModel,
                        onRegresarClick = {
                            mostrarPantallaJuego = false // <- Regresa al menú
                        }
                    )
                } else {
                    MenuInicial(
                        onStartClick = {

                            mostrarPantallaJuego = true
                        },
                        onExitClick = {

                            finish()
                        },
                        viewmodel = relojViewModel,
                        audioViewModel = audioViewModel // <--- este es el que faltaba
                    )
                }


                // Iniciar música de fondo al abrir app
                LaunchedEffect(Unit) {
                    audioViewModel.startBackgroundMusic()
                }


            }
        }
    }
}
