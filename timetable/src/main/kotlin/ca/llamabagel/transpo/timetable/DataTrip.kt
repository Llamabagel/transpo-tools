package ca.llamabagel.transpo.timetable

import ca.llamabagel.transpo.models.gtfs.StopTime

data class DataTrip(val stopId: String, val sequence: Int)

fun StopTime.asDataTrip(): DataTrip = DataTrip(stopId.value, stopSequence)