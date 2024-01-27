package com.example.wls_android.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface WlsApi {
    @GET("disturbances")
    suspend fun getDisturbances(
        @Query("line") line: String? = null, // Linie (z.B. U6, 31)
        @Query("type") type: String? = null, // Störungstyp (0 = Bus, 1 = Straßenbahn, 2 = U-Bahn)
        @Query("from") from: String = LocalDate.now() // Startdatum bzw. nur Störungen ab diesem Datum werden angezeigt (YYYY-MM-DD)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        @Query("to") to: String = LocalDate.now() // Enddatum bzw. nur Störungen bis zu diesem Datum werden angezeigt (YYYY-MM-DD)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        @Query("order") order: String? = null, // Gibt den Wert an nach dem sortiert werden soll (start, end, type)
        @Query("desc") desc: Boolean? = null, // Gibt an ob Störungen auf- oder absteigend aufgelistet werden sollen
        @Query("active") active: Boolean? = null // Störungen die noch nicht beendet sind, werden noch angezeigt
    ) : Response<com.example.wls_android.data.Data>
}