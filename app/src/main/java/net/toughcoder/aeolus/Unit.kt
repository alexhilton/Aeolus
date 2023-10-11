package net.toughcoder.aeolus

sealed class Unit(
    val temp: String,
    val length: String,
    val speed: String,
    val pressure: String,
    val scale: String,
    val percent: String
)

class MetricUnit : Unit(
    "度",
    "km",
    "km/h",
    "hPa",
    "级",
    "%"
)

fun unit(): Unit = MetricUnit()