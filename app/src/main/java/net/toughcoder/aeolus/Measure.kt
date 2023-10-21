package net.toughcoder.aeolus

sealed class Measure(
    val temp: String,
    val length: String,
    val speed: String,
    val pressure: String,
    val scale: String,
    val percent: String
)

class Metric : Measure(
    "\u2103",
    "km",
    "km/h",
    "hPa",
    "级",
    "%"
)

fun unit(): Measure = Metric()