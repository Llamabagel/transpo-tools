package ca.llamabagel.transpo.tools.pack.transformers

import ca.llamabagel.transpo.models.app.Route

object AppRoutesTransformer : DataTransformer<Route>() {

    override fun removeItem(item: Route): Boolean = false

    override fun mapItem(item: Route): Route {
        // TODO: Add correct color codes
        return when (item.shortName.toInt()) {
            1 -> item.copy(serviceLevel = "confederation", color = "#FF0000")
            2 -> item.copy(serviceLevel = "trillium", color = "#00FF00")
            6, 7, in 10..12, 14, 16, 40, 44, 80, 85, 87, 88, 104, 111 -> item.copy(serviceLevel = "frequent", color = "#FFF000")
            45, in 61..63, 94, 95, in 97..99 -> item.copy(serviceLevel = "rapid", color = "#0000FF")
            in 200..299 -> item.copy(serviceLevel = "connexion", color = "#FF00FF")
            else -> item.copy(serviceLevel = "local", color = "#212121")
        }
    }

    override fun injectItems(): List<Route> = emptyList()
}