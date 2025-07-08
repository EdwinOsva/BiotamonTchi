package com.example.biotamontchi.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun PanelAlimentos(
    mascota: Mascota,
    onCerrar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            // Botón regresar
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
                    .fillMaxHeight(0.5f),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x65BCFFA0) // <--- Color con transparencia personalizado
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fila 1:
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.comi1),
                            contentDescription = "Botón comida1",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$5",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi2),
                            contentDescription = "Botón comida2",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$6",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Image(
                            painter = painterResource(id = R.drawable.comi6),
                            contentDescription = "Botón comida3",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$6",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi11),
                            contentDescription = "Botón comida4",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )

                        Text(
                            text = "$7",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    // Fila 2: 2 imágenes + $1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.comi13),
                            contentDescription = "Botón comida5",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$9",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi4),
                            contentDescription = "Botón comida6",
                            modifier = Modifier
                                .size(70.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$10",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi5),
                            contentDescription = "Botón comida7",
                            modifier = Modifier
                                .size(70.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$12",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
//fila3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.comi7),
                            contentDescription = "Botón comida8",
                            modifier = Modifier
                                .size(70.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$13",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi3),
                            contentDescription = "Botón comida9",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$14",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi8),
                            contentDescription = "Botón comida10",
                            modifier = Modifier
                                .size(70.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$14",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    //fila4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.comi12),
                            contentDescription = "Botón comida11",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$15",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi9),
                            contentDescription = "Botón comida12",
                            modifier = Modifier
                                .size(80.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$15",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.comi10),
                            contentDescription = "Botón comida13",
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    // Acción para regresar
                                    // premio1()
                                }
                        )
                        Text(
                            text = "$17",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }


            }
        }
    }
}


