package net.toughcoder.aeolus.model

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

class Imperial : Measure(
    "\u2109",
    "mi",
    "mi/h",
    "hPa",
    "Grade",
    "%"
)

val MEASURE_MAP = mapOf(
    MEASURE_METRIC to Metric(),
    MEASURE_IMPERIAL to Imperial()
)

fun WeatherNow.getMeasure(): Measure {
    val key = if (measure in MEASURE_MAP) measure else MEASURE_METRIC
    return MEASURE_MAP[key]!!
}

fun DailyWeather.getMeasure(): Measure {
    val key = if (measure in MEASURE_MAP) measure else MEASURE_METRIC
    return MEASURE_MAP[key]!!
}

fun HourlyWeather.getMeasure(): Measure {
    val key = if (measure in MEASURE_MAP) measure else MEASURE_METRIC
    return MEASURE_MAP[key]!!
}