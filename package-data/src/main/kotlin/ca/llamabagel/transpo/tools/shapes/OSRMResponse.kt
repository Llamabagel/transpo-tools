package ca.llamabagel.transpo.tools.shapes

data class OSRMResponse(
    val distance: Double,
    val duration: Double,
    val weight: Double,
    val weightName: String,
    val geometry: String,
    val legs: List<OSRMRouteLeg>
)

data class OSRMGeometry(
    val type: String,
    val coordinates: List<Array<Double>>
)

data class OSRMRouteLeg(
    val distance: Double,
    val duration: Double
)