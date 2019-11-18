package ca.llamabagel.transpo.tools.shapes.model

data class Response(
    val code: String,
    val routes: List<Route>
)

data class Route(
    val distance: Double,
    val duration: Double,
    val geometry: String,
    val weight: Double,
    val weightName: String
)