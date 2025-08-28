package com.example.biotamontchi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biotamontchi.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.biotamontchi.data.ObjetoPremio
import com.example.biotamontchi.data.PrefsManager
import com.example.biotamontchi.data.PremioEnPantalla
import com.example.biotamontchi.data.TipoPremio
import com.example.biotamontchi.data.premiar




@Composable
fun PanelPremios(
    onCerrar: () -> Unit,
    premiosEnPantalla: MutableList<PremioEnPantalla>,
    prefs: PrefsManager,
    monedas: MutableState<Int>
)  {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    val premiosDisponibles = listOf(
        ObjetoPremio(id = 0, nombre = "Premio1", costo = 200, drawable = R.drawable.premio1, tamano = 80.dp, sonido= R.raw.premio1, tipo = TipoPremio.PAPALOTE),
        ObjetoPremio(id = 1, nombre = "Premio2", costo = 200, drawable = R.drawable.premio2, tamano = 50.dp, sonido= R.raw.premio3, tipo = TipoPremio.PELOTA),
        ObjetoPremio(id = 2, nombre = "Premio3", costo = 200, drawable = R.drawable.premio3, tamano = 80.dp, sonido= R.raw.premio2, tipo = TipoPremio.BATE),
        ObjetoPremio(id = 3, nombre = "Premio4", costo = 300, drawable = R.drawable.premio4, tamano = 80.dp, sonido= R.raw.premio41, tipo = TipoPremio.SAXOFON),
        ObjetoPremio(id = 4, nombre = "Premio5", costo = 1000, drawable = R.drawable.premio5, tamano = 90.dp, sonido= R.raw.comer, tipo = TipoPremio.TORTUGA),
        ObjetoPremio(id = 5, nombre = "Premio6", costo = 1000, drawable = R.drawable.premio6_1, tamano = 90.dp, sonido=  R.raw.comer, tipo = TipoPremio.TORTUGA)

    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88BB9D02))
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
                    .fillMaxHeight(0.6f),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x65E2D595) // <--- Color con transparencia personalizado
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fila 1: 2 imágenes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.premio2),
                            contentDescription = "Botón premio2",
                            modifier = Modifier
                                .size(70.dp)
                                .clickable {
                                    // Acción para regresar
                                    premiar(
                                        premio = premiosDisponibles[1],
                                        premiosEnPantalla = premiosEnPantalla,
                                        prefs = prefs,
                                        monedas = monedas,
                                        screenWidthDp = screenWidth,
                                        screenHeightDp = screenHeight
                                    )
                                }
                        )
                        val desbloqueado1 = prefs.premioEstaDesbloqueado(premiosDisponibles[1].id)
                        val textoPrecio1 = if (desbloqueado1) "$0" else "$${premiosDisponibles[1].costo}"

                        Text(
                            text = textoPrecio1,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.premio3),
                            contentDescription = "Botón premio3",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    // Acción para regresar
                                    premiar(
                                        premio = premiosDisponibles[2],
                                        premiosEnPantalla = premiosEnPantalla,
                                        prefs = prefs,
                                        monedas = monedas,
                                        screenWidthDp = screenWidth,
                                        screenHeightDp = screenHeight
                                    )
                                }
                        )
                        val desbloqueado2 = prefs.premioEstaDesbloqueado(premiosDisponibles[2].id)
                        val textoPrecio2 = if (desbloqueado2) "$0" else "$${premiosDisponibles[2].costo}"

                        Text(
                            text = textoPrecio2,
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
                            painter = painterResource(id = R.drawable.premio4),
                            contentDescription = "Botón premio4",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    // Acción para regresar
                                    premiar(
                                        premio = premiosDisponibles[3],
                                        premiosEnPantalla = premiosEnPantalla,
                                        prefs = prefs,
                                        monedas = monedas,
                                        screenWidthDp = screenWidth,
                                        screenHeightDp = screenHeight
                                    )
                                }
                        )
                        val desbloqueado3 = prefs.premioEstaDesbloqueado(premiosDisponibles[3].id)
                        val textoPrecio3 = if (desbloqueado3) "$0" else "$${premiosDisponibles[3].costo}"

                        Text(
                            text = textoPrecio3,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.premio5),
                            contentDescription = "Botón premio5",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    // Acción para regresar
                                    premiar(
                                        premio = premiosDisponibles[4],
                                        premiosEnPantalla = premiosEnPantalla,
                                        prefs = prefs,
                                        monedas = monedas,
                                        screenWidthDp = screenWidth,
                                        screenHeightDp = screenHeight
                                    )

                                }
                        )
                        val desbloqueado4 = prefs.premioEstaDesbloqueado(premiosDisponibles[4].id)
                        val textoPrecio4 = if (desbloqueado4) "$0" else "$${premiosDisponibles[4].costo}"

                        Text(
                            text = textoPrecio4,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.premio1),
                                contentDescription = "Botón premio1",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        // Acción para regresar
                                        premiar(
                                            premio = premiosDisponibles[0],
                                            premiosEnPantalla = premiosEnPantalla,
                                            prefs = prefs,
                                            monedas = monedas,
                                            screenWidthDp = screenWidth,
                                            screenHeightDp = screenHeight
                                        )
                                        onCerrar()
                                    }
                            )
                            val desbloqueado0 = prefs.premioEstaDesbloqueado(premiosDisponibles[0].id)
                            val textoPrecio0 = if (desbloqueado0) "$0" else "$${premiosDisponibles[0].costo}"

                            Text(
                                text = textoPrecio0,
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

