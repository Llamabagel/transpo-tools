package ca.llamabagel.transpo.tools.pack.transformers

import ca.llamabagel.transpo.models.gtfs.Stop
import ca.llamabagel.transpo.models.gtfs.StopId
import ca.llamabagel.transpo.models.gtfs.asStopId

object StopsTransformer : DataTransformer<Stop>() {

    /**
     * Object containing [Stop] custom definitions for stations in the OC Transpo system.
     * These values are used in [assignParentStations] and [injectItems] to add proper station data
     * to the dataset used by the client apps.
     *
     * Here, the [Stop.id] is the abbreviated letter code used by OC Transpo to identify the station for 560560 purposes.
     */
    object ParentStations {
        private val stop = Stop("".asStopId()!!, null, "", null, 0.0, 0.0, null, null, null, null, null, null)

        val baseline = stop.copy(id = StopId("BAS"), code = "3017", name = "Baseline Station", latitude = 45.347350, longitude = -75.761676, locationType = 1)
        val bayshore = stop.copy(id = StopId("BAY"), code = "3050", name = "Bayshore Station", latitude = 45.345667, longitude = -75.809571, locationType = 1)
        val bayview = stop.copy(id = StopId("BAYV"), code = "3060", name = "Bayview Station", latitude = 45.409326, longitude = -75.722008, locationType = 1)
        val beatrice = stop.copy(id = StopId("BEA"), code = "3049", name = "Beatrice Station", latitude = 45.271683, longitude = -75.725935, locationType = 1)
        val billingsBridge = stop.copy(id = StopId("BIB"), code = "3034", name = "Billings Bridge Station", latitude = 45.384771, longitude = -75.676710, locationType = 1)
        val blair = stop.copy(id = StopId("BLR"), code = "3027", name = "Blair Station", latitude = 45.431213, longitude = -75.608409, locationType = 1)
        val canadianTireCentre = stop.copy(id = StopId("CTC"), code = "3059", name = "Canadian Tire Centre", latitude = 45.298810, longitude = -75.926695, locationType = 1)
        val carleton = stop.copy(id = StopId("CARL"), code = "3062", name = "Carleton Station", latitude = 45.385593, longitude = -75.695920, locationType = 1)
        val carling = stop.copy(id = StopId("CAR"), code = "3061", name = "Carling Station", latitude = 45.397402, longitude = -75.709457, locationType =  1)
        val dominion = stop.copy(id = StopId("DOM"), code = "3013", name = "Dominion Station", latitude = 45.392224, longitude = -75.760621, locationType = 1)
        val eaglesonEast = stop.copy(id = StopId("EAG_E"), code = "3055", name = "Eagleson East", latitude = 45.318573, longitude = -75.884001, locationType = 1)
        val eaglesonWest = stop.copy(id = StopId("EAG_W"), code = "5855", name = "Eagleson West", latitude = 45.317963, longitude = -75.884743, locationType = 1)
        val fallowfield = stop.copy(id = StopId("FAL"), code = "3043", name = "Fallowfield Station", latitude = 45.298792, longitude = -75.736046, locationType = 1)
        val greenboro = stop.copy(id = StopId("GRE"), code = "3037", name = "Greenboro Station", latitude = 45.359866, longitude = -75.658802, locationType = 1)
        val heron = stop.copy(id = StopId("HER"), code = "3035", name = "Heron Station", latitude = 45.378776, longitude = -75.679685, locationType = 1)
        val hurdman = stop.copy(id = StopId("HUR"), code = "3023", name = "Hurdman Station", latitude = 45.412365, longitude = -75.664320, locationType = 1)
        val innovation = stop.copy(id = StopId("INN"), code = "3057", name = "Innovation Station", latitude = 45.342992, longitude = -75.932023, locationType = 1)
        val iris = stop.copy(id = StopId("IRS"), code = "3016", name = "Iris Station", latitude = 45.355793, longitude = -75.769786, locationType = 1)
        val jeanneDarc = stop.copy(id = StopId("JDARC"), code = "3070", name = "Jeanne D'Arc Station", latitude = 45.469094, longitude = -75.546012, locationType = 1)
        val laurier = stop.copy(id = StopId("LAU"), code = "3020", name = "Laurier Station", latitude = 45.424207, longitude = -75.686817, locationType = 1)
        val lebreton = stop.copy(id = StopId("LEB"), code = "3010", name = "LeBreton Station", latitude = 45.413069, longitude = -75.712779, locationType = 1)
        val lees = stop.copy(id = StopId("LEE"), code = "3022", name = "Lees Station", latitude = 45.416066, longitude = -75.670350, locationType = 1)
        val leitrim = stop.copy(id = StopId("LEI"), code = "3041", name = "Leitrim Station", latitude = 45.314301, longitude = -75.630865, locationType = 1)
        val lincolnFields = stop.copy(id = StopId("LIN"), code = "3014", name = "Lincoln Fields Station", latitude = 45.285409, longitude = -75.746888, locationType = 1)
        val longfields = stop.copy(id = StopId("LNGF"), code = "3046", name = "Longfields Station", latitude = 45.347350, longitude = -75.761676, locationType = 1)
        val lyceeClaudel = stop.copy(id = StopId("LYC"), code = "3030", name = "Lycée Claudel Station", latitude = 45.406512, longitude = -75.664353, locationType = 1)
        val mackenzieKing = stop.copy(id = StopId("MAC"), code = "3000", name = "Mackenzie King Station", latitude = 45.424237, longitude = -75.689459, locationType = 1)
        val marketplace = stop.copy(id = StopId("MKPL"), code = "3047", name = "Marketplace Station", latitude = 45.269412, longitude = -75.742637, locationType = 1)
        val millennium = stop.copy(id = StopId("MIL"), code = "3076", name = "Millenium Station", latitude = 45.465634, longitude = -75.448595, locationType = 1)
        val moodie = stop.copy(id = StopId("MOOD"), code = "3045", name = "Moodie Station", latitude = 45.341011, longitude = -75.836959, locationType = 1)
        val mooneysBay = stop.copy(id = StopId("MOO"), code = "3063", name = "Mooney's Bay Station", latitude = 45.376895, longitude = -75.685186, locationType = 1)
        val nepeanWoods = stop.copy(id = StopId("NEP"), code = "3048", name = "Nepean Woods Station", latitude = 45.274674, longitude = -75.716582, locationType = 1)
        val pinecrest = stop.copy(id = StopId("PIN"), code = "3019", name = "Pinecrest Station", latitude = 45.350616, longitude = -75.791291, locationType = 1)
        val placeDOrleans = stop.copy(id = StopId("ORL"), code = "3028", name = "Place D'Orléans Station", latitude = 45.478538, longitude = -75.518910, locationType = 1)
        val pleasantPark = stop.copy(id = StopId("PPK"), code = "3033", name = "Pleasant Park Station", latitude = 45.392749, longitude = -75.669366, locationType = 1)
        val queensway = stop.copy(id = StopId("QWY"), code = "3015", name = "Queensway Station", latitude = 45.359160, longitude = -75.771980, locationType = 1)
        val rideau = stop.copy(id = StopId("RID"), code = "3009", name = "Rideau Station", latitude = 45.426614, longitude = -75.690743, locationType = 1)
        val riverside = stop.copy(id = StopId("RIV"), code = "3032", name = "Riverside Station", latitude = 45.396586, longitude = -75.669399, locationType = 1)
        val riverview = stop.copy(id = StopId("RIVR"), code = "3040", name = "Riverview Station", latitude = 45.268668, longitude = -75.694910, locationType = 1)
        val smyth = stop.copy(id = StopId("SMY"), code = "3031", name = "Smyth Station", latitude = 45.401320, longitude = -75.666586, locationType = 1)
        val southKeys = stop.copy(id = StopId("SKE"), code = "3038", name = "South Keys Station", latitude = 45.353530, longitude = -75.655143, locationType = 1)
        val stLaurent = stop.copy(id = StopId("STL"), code = "3025", name = "St. Laurent Station", latitude = 45.420569, longitude = -75.638137, locationType = 1)
        val strandherd = stop.copy(id = StopId("STR"), code = "3044", name = "Strandherd Station", latitude = 45.273333, longitude = -75.745543, locationType = 1)
        val terryFox = stop.copy(id = StopId("TFO"), code = "3058", name = "Terry Fox Station", latitude = 45.309308, longitude = -75.907086, locationType = 1)
        val train = stop.copy(id = StopId("TRA"), code = "3024", name = "Train Station", latitude = 45.416806, longitude = -75.651817, locationType = 1)
        val trim = stop.copy(id = StopId("TRI"), code = "3029", name = "Trim Station", latitude = 45.493329, longitude = -75.480162, locationType = 1)
        val tunneysPasture = stop.copy(id = StopId("TUP"), code = "3011", name = "Tunney's Pasture Station", latitude = 45.403490, longitude = -75.736268, locationType = 1)
        val walkley = stop.copy(id = StopId("WAL"), code = "3036", name = "Walkley Station", latitude = 45.368809, longitude = -75.667204, locationType = 1)
        val westboro = stop.copy(id = StopId("WEB"), code = "3012", name = "Westboro Station", latitude = 45.396526, longitude = -75.752261, locationType = 1)

        fun getList(): List<Stop> = listOf(baseline, bayshore, bayview, beatrice, billingsBridge, blair, canadianTireCentre,
            carleton, carling, dominion, eaglesonEast, eaglesonWest, fallowfield, greenboro, heron, hurdman, innovation,
            iris, jeanneDarc, laurier, lebreton, lees, leitrim, lincolnFields, longfields, lyceeClaudel, mackenzieKing,
            marketplace, millennium, moodie, mooneysBay, nepeanWoods, pinecrest, placeDOrleans, pleasantPark, queensway,
            rideau, riverside, riverview, smyth, southKeys, stLaurent, strandherd, terryFox, train, trim, tunneysPasture,
            walkley, westboro)
    }

    override fun removeItem(item: Stop): Boolean = false

    override fun mapItem(item: Stop): Stop {
        var mapped = item
        mapped = assignParentStations(mapped)
        if (item != mapped) {
            println("Mapped (parent station) $item to $mapped")
        }

        mapped = correctNames(mapped)
        if (item != mapped) {
            println("Corrected ${item.name} to ${mapped.name}")
        }

        return mapped
    }

    override fun injectItems(): List<Stop> {
        println("Appended ${ParentStations.getList()}")
        return ParentStations.getList()
    }

    /**
     * Given a [Stop] [item], find a matching station (if any) and assign the [item]'s parent station to be the matching
     * station's id. Parent stations are defined in [ParentStations] and are matched by their [Stop.code]s.
     *
     * @return The given stop item with its matching parent station, or the unmodified item if the stop does not belong to a station.
     */
    private fun assignParentStations(item: Stop): Stop {
        return when (item.code) {
            ParentStations.baseline.code -> item.copy(locationType = 0, parentStation = ParentStations.baseline.id)
            ParentStations.bayshore.code -> item.copy(locationType = 0, parentStation = ParentStations.bayshore.id)
            ParentStations.bayview.code -> item.copy(locationType = 0, parentStation = ParentStations.bayview.id)
            ParentStations.beatrice.code -> item.copy(locationType = 0, parentStation = ParentStations.beatrice.id)
            ParentStations.billingsBridge.code -> item.copy(locationType = 0, parentStation = ParentStations.billingsBridge.id)
            ParentStations.blair.code -> item.copy(locationType = 0, parentStation = ParentStations.blair.id)
            ParentStations.canadianTireCentre.code -> item.copy(locationType = 0, parentStation = ParentStations.canadianTireCentre.id)
            ParentStations.carleton.code -> item.copy(locationType = 0, parentStation = ParentStations.carleton.id)
            ParentStations.dominion.code -> item.copy(locationType = 0, parentStation = ParentStations.dominion.id)
            ParentStations.eaglesonEast.code -> item.copy(locationType = 0, parentStation = ParentStations.eaglesonEast.id)
            ParentStations.eaglesonWest.code -> item.copy(locationType = 0, parentStation = ParentStations.eaglesonWest.id)
            ParentStations.fallowfield.code -> item.copy(locationType = 0, parentStation = ParentStations.fallowfield.id)
            ParentStations.greenboro.code -> item.copy(locationType = 0, parentStation = ParentStations.greenboro.id)
            ParentStations.heron.code -> item.copy(locationType = 0, parentStation = ParentStations.heron.id)
            ParentStations.hurdman.code -> item.copy(locationType = 0, parentStation = ParentStations.hurdman.id)
            ParentStations.innovation.code -> item.copy(locationType = 0, parentStation = ParentStations.innovation.id)
            ParentStations.iris.code -> item.copy(locationType = 0, parentStation = ParentStations.iris.id)
            ParentStations.jeanneDarc.code -> item.copy(locationType = 0, parentStation = ParentStations.jeanneDarc.id)
            ParentStations.laurier.code -> item.copy(locationType = 0, parentStation = ParentStations.laurier.id)
            ParentStations.lebreton.code -> item.copy(locationType = 0, parentStation = ParentStations.lebreton.id)
            ParentStations.lees.code -> item.copy(locationType = 0, parentStation = ParentStations.lees.id)
            ParentStations.leitrim.code -> item.copy(locationType = 0, parentStation = ParentStations.leitrim.id)
            ParentStations.lincolnFields.code -> item.copy(locationType = 0, parentStation = ParentStations.lincolnFields.id)
            ParentStations.longfields.code -> item.copy(locationType = 0, parentStation = ParentStations.longfields.id)
            ParentStations.lyceeClaudel.code -> item.copy(locationType = 0, parentStation = ParentStations.lyceeClaudel.id)
            ParentStations.mackenzieKing.code -> item.copy(locationType = 0, parentStation = ParentStations.mackenzieKing.id)
            ParentStations.marketplace.code -> item.copy(locationType = 0, parentStation = ParentStations.marketplace.id)
            ParentStations.millennium.code -> item.copy(locationType = 0, parentStation = ParentStations.millennium.id)
            ParentStations.moodie.code -> item.copy(locationType = 0, parentStation = ParentStations.moodie.id)
            ParentStations.mooneysBay.code -> item.copy(locationType = 0, parentStation = ParentStations.mooneysBay.id)
            ParentStations.nepeanWoods.code -> item.copy(locationType = 0, parentStation = ParentStations.nepeanWoods.id)
            ParentStations.pinecrest.code -> item.copy(locationType = 0, parentStation = ParentStations.pinecrest.id)
            ParentStations.placeDOrleans.code -> item.copy(locationType = 0, parentStation = ParentStations.placeDOrleans.id)
            ParentStations.pleasantPark.code -> item.copy(locationType = 0, parentStation = ParentStations.pleasantPark.id)
            ParentStations.queensway.code -> item.copy(locationType = 0, parentStation = ParentStations.queensway.id)
            ParentStations.rideau.code -> item.copy(locationType = 0, parentStation = ParentStations.rideau.id)
            ParentStations.riverside.code -> item.copy(locationType = 0, parentStation = ParentStations.riverside.id)
            ParentStations.riverview.code -> item.copy(locationType = 0, parentStation = ParentStations.riverview.id)
            ParentStations.smyth.code -> item.copy(locationType = 0, parentStation = ParentStations.smyth.id)
            ParentStations.southKeys.code -> item.copy(locationType = 0, parentStation = ParentStations.southKeys.id)
            ParentStations.stLaurent.code -> item.copy(locationType = 0, parentStation = ParentStations.stLaurent.id)
            ParentStations.strandherd.code -> item.copy(locationType = 0, parentStation = ParentStations.strandherd.id)
            ParentStations.terryFox.code -> item.copy(locationType = 0, parentStation = ParentStations.terryFox.id)
            ParentStations.train.code -> item.copy(locationType = 0, parentStation = ParentStations.train.id)
            ParentStations.trim.code -> item.copy(locationType = 0, parentStation = ParentStations.trim.id)
            ParentStations.tunneysPasture.code -> item.copy(locationType = 0, parentStation = ParentStations.tunneysPasture.id)
            ParentStations.walkley.code -> item.copy(locationType = 0, parentStation = ParentStations.walkley.id)
            ParentStations.westboro.code -> item.copy(locationType = 0, parentStation = ParentStations.westboro.id)
            else -> item
        }
    }

    private fun correctNames(item: Stop): Stop = item.copy(name = upperCaseAllFirst(item.name))

    /**
     * Converts the ALL CAPS stop names used in GTFS to more human-readable Title Case names.
     */
    private fun upperCaseAllFirst(value: String): String {
        val array = value.toCharArray()
        // Uppercase first letter.
        array[0] = Character.toUpperCase(array[0])

        // Uppercase all letters that follow a whitespace character, or a number
        for (i in 1 until array.size) {
            if (Character.isWhitespace(array[i - 1]) || Character.isDigit(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i])
            } else {
                array[i] = Character.toLowerCase(array[i])
            }
        }

        return String(array)
    }
}