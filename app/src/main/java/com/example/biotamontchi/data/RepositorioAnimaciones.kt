package com.example.biotamontchi.data

import com.example.biotamontchi.R

object RepositorioAnimaciones {

    val animaciones: Map<Int, Map<Etapa, Map<String, Map<String, List<Int>>>>> = mapOf(
        1 to mapOf( // tipoBiotamon: 1 = Plantas

            Etapa.SEMBRAR to mapOf(
                "normal" to mapOf(
                    "margarita" to listOf(
                        R.drawable.sembrar1,
                        R.drawable.sembrar2,
                        R.drawable.sembrar3,
                        R.drawable.sembrar4,
                        R.drawable.sembrar5,
                        R.drawable.sembrar6,
                        R.drawable.sembrar5,
                        R.drawable.sembrar6,
                    ),
                    "amapola" to listOf(
                        R.drawable.sembrar1,
                        R.drawable.sembrar2,
                        R.drawable.sembrar3,
                        R.drawable.sembrar4,
                        R.drawable.sembrar5,
                        R.drawable.sembrar6,
                        R.drawable.sembrar5,
                        R.drawable.sembrar6,
                    ),
                    "lili" to listOf(
                        R.drawable.sembrar1,
                        R.drawable.sembrar2,
                        R.drawable.sembrar3,
                        R.drawable.sembrar4,
                        R.drawable.sembrar5,
                        R.drawable.sembrar6,
                        R.drawable.sembrar5,
                        R.drawable.sembrar6,
                    )
                )
            ),

            Etapa.SEMILLA to mapOf(
                "normal" to mapOf(
                    "margarita" to listOf(
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                    ),
                    "amapola" to listOf(
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                    ),
                    "lili" to listOf(
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                        R.drawable.semilla1,
                        R.drawable.semilla2,
                    )
                ),
                "seco" to mapOf(
                    "margarita" to listOf(
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                    ),
                    "amapola" to listOf(
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                    ),
                    "lili" to listOf(
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                        R.drawable.semillaseco1,
                        R.drawable.semillaseco2,
                    )
                )
            ),


                    Etapa.PLANTULA to mapOf(
                "normal" to mapOf(
                    "margarita" to listOf(
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                    ),
                    "amapola" to listOf(
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                    ),
                    "lili" to listOf(
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                        R.drawable.plantula1,
                        R.drawable.plantula2,
                    )
                ),
                        "seco" to mapOf(
                            "margarita" to listOf(
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                            ),
                            "amapola" to listOf(
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                            ),
                            "lili" to listOf(
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                                R.drawable.plantulaseca1,
                                R.drawable.plantulaseca2,
                            )
                        )
            ),

            Etapa.PLANTA to mapOf(
                "normal" to mapOf(
                    "margarita" to listOf(
                        R.drawable.planta1,
                        R.drawable.planta2,
                        R.drawable.planta3,
                        R.drawable.planta4,
                        R.drawable.planta5,
                        R.drawable.planta2,
                        R.drawable.planta3,
                        R.drawable.planta4,
                    ),
                    "amapola" to listOf(
                        R.drawable.planta1,
                        R.drawable.planta2,
                        R.drawable.planta3,
                        R.drawable.planta4,
                        R.drawable.planta5,
                        R.drawable.planta2,
                        R.drawable.planta3,
                        R.drawable.planta4,
                    ),
                    "lili" to listOf(
                        R.drawable.planta1,
                        R.drawable.planta2,
                        R.drawable.planta3,
                        R.drawable.planta4,
                        R.drawable.planta5,
                        R.drawable.planta2,
                        R.drawable.planta3,
                        R.drawable.planta4,
                    )
                ),
                "seco" to mapOf(
                    "margarita" to listOf(
                        R.drawable.plantaseca1,
                        R.drawable.plantaseca2,
                        R.drawable.plantaseca3,
                        R.drawable.plantaseca4,
                        R.drawable.plantaseca1,
                        R.drawable.plantaseca2,
                        R.drawable.plantaseca3,
                        R.drawable.plantaseca4,
                    ),
                    "amapola" to listOf(
                        R.drawable.plantaseca1,
                        R.drawable.plantaseca2,
                        R.drawable.plantaseca3,
                        R.drawable.plantaseca4,
                        R.drawable.plantaseca1,
                        R.drawable.plantaseca2,
                        R.drawable.plantaseca3,
                        R.drawable.plantaseca4,
                    ),
                    "lili" to listOf(
                        R.drawable.plantaseca1,
                        R.drawable.plantaseca2,
                        R.drawable.plantaseca3,
                        R.drawable.plantaseca4,
                        R.drawable.plantaseca1,
                        R.drawable.plantaseca2,
                        R.drawable.plantaseca3,
                        R.drawable.plantaseca4,
                    )
                )
            ),

            Etapa.MADURA to mapOf(
                "normal" to mapOf(
                    "margarita" to listOf(
                        R.drawable.madura1,
                        R.drawable.madura2,
                        R.drawable.madura3,
                        R.drawable.madura4,
                        R.drawable.madura3,
                        R.drawable.madura2,
                    ),
                    "amapola" to listOf(
                        R.drawable.amapola00,
                        R.drawable.amapola01,
                        R.drawable.amapola02,
                        R.drawable.amapola03,
                        R.drawable.amapola04,
                        R.drawable.amapola05,
                        R.drawable.amapola06,
                        R.drawable.amapola07,
                    ),
                    "lili" to listOf(
                        R.drawable.lili00,
                        R.drawable.lili01,
                        R.drawable.lili02,
                        R.drawable.lili03,
                        R.drawable.lili04,
                        R.drawable.lili05,
                        R.drawable.lili06,
                        R.drawable.lili07,
                    )
                ),
                "seco" to mapOf(
                    "margarita" to listOf(
                        R.drawable.marchita1,
                        R.drawable.marchita2,
                        R.drawable.marchita3,
                        R.drawable.marchita4,
                        R.drawable.marchita1,
                        R.drawable.marchita2,
                        R.drawable.marchita3,
                        R.drawable.marchita4,
                    ),
                    "amapola" to listOf(
                        R.drawable.amapolamuerta01,
                        R.drawable.amapolamuerta02,
                        R.drawable.amapolamuerta03,
                        R.drawable.amapolamuerta04,
                        R.drawable.amapolamuerta01,
                        R.drawable.amapolamuerta02,
                        R.drawable.amapolamuerta03,
                        R.drawable.amapolamuerta04,
                    ),
                    "lili" to listOf(
                        R.drawable.lilimarchita00,
                        R.drawable.lilimarchita01,
                        R.drawable.lilimarchita02,
                        R.drawable.lilimarchita03,
                        R.drawable.lilimarchita00,
                        R.drawable.lilimarchita01,
                        R.drawable.lilimarchita02,
                        R.drawable.lilimarchita03,
                    )
                )

            ),

            Etapa.MARCHITA to mapOf(
                "normal" to mapOf(
                    "margarita" to listOf(
                        R.drawable.marchita1,
                        R.drawable.marchita2,
                        R.drawable.marchita3,
                        R.drawable.marchita4,
                        R.drawable.marchita1,
                        R.drawable.marchita2,
                        R.drawable.marchita3,
                        R.drawable.marchita4,
                    ),
                    "amapola" to listOf(
                        R.drawable.amapolamuerta01,
                        R.drawable.amapolamuerta02,
                        R.drawable.amapolamuerta03,
                        R.drawable.amapolamuerta04,
                        R.drawable.amapolamuerta01,
                        R.drawable.amapolamuerta02,
                        R.drawable.amapolamuerta03,
                        R.drawable.amapolamuerta04,
                    ),
                    "lili" to listOf(
                        R.drawable.lilimarchita00,
                        R.drawable.lilimarchita01,
                        R.drawable.lilimarchita02,
                        R.drawable.lilimarchita03,
                        R.drawable.lilimarchita00,
                        R.drawable.lilimarchita01,
                        R.drawable.lilimarchita02,
                        R.drawable.lilimarchita03,
                    )
                ),
                "seco" to mapOf(
                    "margarita" to listOf(
                        R.drawable.marchita1,
                        R.drawable.marchita2,
                        R.drawable.marchita3,
                        R.drawable.marchita4,
                        R.drawable.marchita1,
                        R.drawable.marchita2,
                        R.drawable.marchita3,
                        R.drawable.marchita4,
                    ),
                    "amapola" to listOf(
                        R.drawable.amapolamuerta01,
                        R.drawable.amapolamuerta02,
                        R.drawable.amapolamuerta03,
                        R.drawable.amapolamuerta04,
                        R.drawable.amapolamuerta01,
                        R.drawable.amapolamuerta02,
                        R.drawable.amapolamuerta03,
                        R.drawable.amapolamuerta04,
                    ),
                    "lili" to listOf(
                        R.drawable.lilimarchita00,
                        R.drawable.lilimarchita01,
                        R.drawable.lilimarchita02,
                        R.drawable.lilimarchita03,
                        R.drawable.lilimarchita00,
                        R.drawable.lilimarchita01,
                        R.drawable.lilimarchita02,
                        R.drawable.lilimarchita03,
                    )
                )
            ),

            Etapa.MUERTA to mapOf(
                "normal" to mapOf(
                    "margarita" to listOf(
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                    ),
                    "amapola" to listOf(
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                    ),
                    "lili" to listOf(
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                        R.drawable.muerta1,
                    )
                ),
                    "seco" to mapOf(
                        "margarita" to listOf(
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                        ),
                        "amapola" to listOf(
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                        ),
                        "lili" to listOf(
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                            R.drawable.muerta1,
                        )
                    )
                )
            )
        )
}

