package ca.llamabagel.transpo.tools.shapes

import ca.llamabagel.transpo.tools.shapes.model.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OSRMService {
    @GET("route/v1/driving/{coordinates}")
    suspend fun route(
        @Path("coordinates") coordinates: String,
        @Query("approaches") approaches: String,
        @Query("alternatives") alternatives: Boolean = false,
        @Query("steps") steps: Boolean = false,
        @Query("overview") overview: String = "full"
    ): Response
}