package com.example.biotamontchi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.biotamontchi.ui.screens.MenuInicial
import com.example.biotamontchi.ui.theme.BiotamonTchiTheme
import com.example.biotamontchi.viewmodel.VistaHoraReloj

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biotamontchi.ui.screens.PantallaPrincipal
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.biotamontchi.model.GameAudioViewModel2
import com.example.biotamontchi.viewmodel.SplashBiotamontchi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BiotamonTchiTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashBiotamontchi {
                        showSplash = false
                    }
                } else {
                    // tu app normal
                    val relojViewModel: VistaHoraReloj = viewModel()
                    val audioViewModel: GameAudioViewModel2 = viewModel()

                    var mostrarPantallaJuego by rememberSaveable { mutableStateOf(false) }

                    if (mostrarPantallaJuego) {
                        PantallaPrincipal(
                            viewModelHora = relojViewModel,
                            onRegresarClick = { mostrarPantallaJuego = false }
                        )
                    } else {
                        MenuInicial(
                            onStartClick = { mostrarPantallaJuego = true },
                            onExitClick = { finish() },
                            viewmodel = relojViewModel,
                            audioViewModel = audioViewModel
                        )
                    }

                    LaunchedEffect(Unit) {
                        audioViewModel.startBackgroundMusic()
                    }
                }
            }
        }
    }
}
