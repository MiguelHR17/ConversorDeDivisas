package dev.miguelehr.conversordedivisas.data.model

data class ConversionRecord(
    val uid: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val amount: Double = 0.0,
    val from: String = "",
    val to: String = "",
    val result: Double = 0.0
)