package com.example.biotamontchi.ui.components


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.biotamontchi.data.Mascota



@Composable
fun PanelAtributos(
    mascota: Mascota,
    onCerrar: () -> Unit
) {
    val atributos = listOf(
        "🌲 Resistencia" to mascota.resistencia,       // Árbol fuerte
        "🌿 Germinación" to mascota.germinacion,       // Brote verde
        "💦 Acuática" to mascota.acuatica,             // Gotas de agua
        "🍃 Aérea" to mascota.aerea,                   // Hoja flotando (aire)
        "🌾 Parásita" to mascota.parasita,             // Hierba entre cultivos
        "🌼 Propagación" to mascota.propagacion,       // Flor esparciendo semillas
        "🌺 Simbiosis" to mascota.simbiosis,           // Flor exótica colaborativa
        "🍂 Adaptación" to mascota.adaptacion          // Hoja seca, cambio de estación
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88D31616))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            // Botón regresar en una fila separada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onCerrar) {
                    Icon(
                        painter = painterResource(id = R.drawable.iconregresar),
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }

            // Tarjeta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x65FAB9B9) // <--- Color con transparencia personalizado
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp) // <--- Menos espacio entre filas
                ) {
                    atributos.forEach { (nombre, valor) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val unidades = if (valor % 10 == 0 && valor != 0) 10 else valor % 10

                            val decenas = valor / 10

                            // Nombre del atributo
                            Text(
                                text = nombre,
                                modifier = Modifier.weight(2.5f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Indicador visual (unidades)
                            Image(
                                painter = painterResource(id = getIndicadorResource(unidades)),
                                contentDescription = "Indicador",
                                modifier = Modifier
                                    .weight(3.5f)
                                    .height(60.dp)
                                    .padding(horizontal = 8.dp),
                                contentScale = ContentScale.FillWidth
                            )

                            // Nivel en decenas
                            Text(
                                text = "Lv $decenas", // O simplemente decenas.toString() si no quieres "Lv"
                                modifier = Modifier.weight(1f),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End
                                )
                            )
                        }
                    }

                }
            }
        }
    }
}




@DrawableRes
fun getIndicadorResource(valor: Int): Int {
    val nivel = valor.coerceIn(0, 10)
    val nombre = "se%02d".format(nivel)
    return when (nombre) {
        "se00" -> R.drawable.se00
        "se01" -> R.drawable.se01
        "se02" -> R.drawable.se02
        "se03" -> R.drawable.se03
        "se04" -> R.drawable.se04
        "se05" -> R.drawable.se05
        "se06" -> R.drawable.se06
        "se07" -> R.drawable.se07
        "se08" -> R.drawable.se08
        "se09" -> R.drawable.se09
        "se10" -> R.drawable.se10
        else -> R.drawable.se00
    }
}


