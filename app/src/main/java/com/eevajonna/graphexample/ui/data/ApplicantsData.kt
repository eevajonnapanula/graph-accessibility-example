package com.eevajonna.graphexample.ui.data

import kotlin.math.roundToInt

data class ApplicantsData(
    val all: Int,
    val women: Int,
    val year: Int,
) {
    private val initialPercentage = women * 100 / all
    val percentage = (initialPercentage * 100.0).roundToInt() / 100.0
}

val uniTech = listOf(
    ApplicantsData(
        9819,
        2577,
        2015,
    ),
    ApplicantsData(
        9582,
        2664,
        2016,
    ),
    ApplicantsData(
        10614,
        3126,
        2017,
    ),
    ApplicantsData(
        12666,
        3708,
        2018,
    ),
    ApplicantsData(
        13269,
        4194,
        2019,
    ),
    ApplicantsData(
        15636,
        4959,
        2020,
    ),
    ApplicantsData(
        17232,
        5517,
        2021,
    ),
    ApplicantsData(
        19578,
        6672,
        2022,
    ),
    ApplicantsData(
        17127,
        5853,
        2023,
    ),
)

val uniIct = listOf(
    ApplicantsData(
        6846,
        1269,
        2015,
    ),
    ApplicantsData(
        6780,
        1536,
        2016,
    ),
    ApplicantsData(
        6744,
        1665,
        2017,
    ),
    ApplicantsData(
        9600,
        2589,
        2018,
    ),
    ApplicantsData(
        10464,
        2919,
        2019,
    ),
    ApplicantsData(
        11736,
        3264,
        2020,
    ),
    ApplicantsData(
        13182,
        3861,
        2021,
    ),
    ApplicantsData(
        14577,
        4452,
        2022,
    ),
    ApplicantsData(
        14346,
        4155,
        2023,
    ),
)

val amkTech = listOf(
    ApplicantsData(
        22494,
        4167,
        2015,
    ),
    ApplicantsData(
        23247,
        4605,
        2016,
    ),
    ApplicantsData(
        22707,
        4707,
        2017,
    ),
    ApplicantsData(
        23091,
        5193,
        2018,
    ),
    ApplicantsData(
        21987,
        5028,
        2019,
    ),
    ApplicantsData(
        25257,
        6240,
        2020,
    ),
    ApplicantsData(
        28107,
        7203,
        2021,
    ),
    ApplicantsData(
        30081,
        8280,
        2022,
    ),
    ApplicantsData(
        34584,
        9342,
        2023,
    ),
)

val amkIct = listOf(
    ApplicantsData(
        13413,
        2574,
        2015,
    ),
    ApplicantsData(
        14133,
        3186,
        2016,
    ),
    ApplicantsData(
        14124,
        3420,
        2017,
    ),
    ApplicantsData(
        15123,
        3942,
        2018,
    ),
    ApplicantsData(
        15816,
        4503,
        2019,
    ),
    ApplicantsData(
        19008,
        5760,
        2020,
    ),
    ApplicantsData(
        21963,
        6786,
        2021,
    ),
    ApplicantsData(
        26685,
        8385,
        2022,
    ),
    ApplicantsData(
        42219,
        13380,
        2023,
    ),
)

val all = getAggregated(listOf(uniIct, uniTech, amkTech, amkIct))
val tech = getAggregated(listOf(uniTech, amkTech))
val ict = getAggregated(listOf(uniIct, amkIct))

fun getAggregated(args: List<List<ApplicantsData>>): List<ApplicantsData> {
    return args.flatten().groupBy { it.year }.values.map { list ->
        val all = list.sumOf { it.all }
        val women = list.sumOf { it.women }
        ApplicantsData(
            year = list.first().year,
            all = all,
            women = women,
        )
    }
}
