package ca.llamabagel.transpo.tools.util

import ca.llamabagel.transpo.models.LatLng
import java.util.ArrayList
import kotlin.math.pow


fun decode(encodedPath: String, precision: Int): List<LatLng> {
    val len = encodedPath.length

    // OSRM uses precision=6, the default Polyline spec divides by 1E5, capping at precision=5
    val factor = 10.0.pow(precision.toDouble())

    // For speed we preallocate to an upper bound on the final length, then
    // truncate the array before returning.
    val path = ArrayList<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int
        do {
            b = encodedPath[index++].toInt() - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        result = 1
        shift = 0
        do {
            b = encodedPath[index++].toInt() - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        path.add(LatLng(lat / factor, lng / factor))
    }

    return path
}