package com.example.test

object PlaygroundRepository {
    val samplePlaygrounds = listOf(
        Playground(
            name = "Na Krejcárku",
            address = "Za Žižkovskou vozovnou 2716/19",
            features = "Skluzavka, houpačka, pískoviště",
            imageRes = R.drawable.na_krejcarku,
        ),
        Playground(
            name = "Riegrovy sady",
            address = "Náměstí Míru, Praha 2",
            features = "Houpačky, prolézačky",
            imageRes = R.drawable.na_krejcarku, // reuse for now
        ),
        Playground(
            name = "Letná",
            address = "Letenské sady, Praha 7",
            features = "Pískoviště, hrazdy",
            imageRes = R.drawable.na_krejcarku,
        ),
    )
}
