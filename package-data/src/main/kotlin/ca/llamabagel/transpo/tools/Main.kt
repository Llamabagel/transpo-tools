@file:JvmName("Main")

package ca.llamabagel.transpo.tools

import ca.llamabagel.transpo.models.gtfs.Stop
import ca.llamabagel.transpo.models.gtfs.StopId

fun main() {
    println("Hello World!")

    val stop = Stop(StopId("AA"), "1234", "Not a Stop", null, -45.0, 75.0, null, null, null, null, null, null)
    println(stop)
}