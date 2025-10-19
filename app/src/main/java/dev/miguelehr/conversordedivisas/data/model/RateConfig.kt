package dev.miguelehr.conversordedivisas.data.model

data class RateConfig(
    val rates: Map<String, Double> = emptyMap()
)