package com.example.biotamontchi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.biotamontchi.R


@Composable
fun FondoDinamico(hora: Int, modifier: Modifier = Modifier) {
    val backgroundId = when (hora) {
        in 0..4 -> R.drawable.tama2       // Noche profunda
        in 5..7 -> R.drawable.tama3       // Amanecer
        in 8..16 -> R.drawable.tama1      // Día claro
        in 17..19 -> R.drawable.tama3     // Atardecer
        in 20..23 -> R.drawable.tama2     // Noche
        else -> R.drawable.tama3          // Seguridad
    }

    Image(
        painter = painterResource(id = backgroundId),
        contentDescription = "Fondo dinámico",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
