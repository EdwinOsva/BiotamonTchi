package com.example.biotamontchi.viewmodel

import android.view.animation.OvershootInterpolator 
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import com.example.biotamontchi.R
@Composable
fun SplashBiotamontchi(onFinish: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Fade in + rebote
        alpha.animateTo(1f, animationSpec = tween(800))
        scale.animateTo(
            1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = { OvershootInterpolator(3f).getInterpolation(it) }
            )
        )
        delay(1500)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x9A8F9629)), // fondo verde
        contentAlignment = Alignment.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.premio6_5), // <- PNG aquí
            contentDescription = "Logo Biotamontchi",
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
        )
    }

}
