package ca.llamabagel.transpo.timetable

import ca.llamabagel.transpo.models.gtfs.StopTime

class DataTrip(val stopId: String, val sequence: Int = 0) {
    override fun equals(other: Any?): Boolean = stopId == (other as? DataTrip)?.stopId

    override fun hashCode(): Int = stopId.hashCode()

    override fun toString(): String = "DataTrip(stopId=$stopId)"
}

data class PartialPath(val start: DataTrip, val end: DataTrip, val others: List<DataTrip> = emptyList())

fun StopTime.asDataTrip(): DataTrip = DataTrip(stopId.value, stopSequence)