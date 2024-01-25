package com.example.wls_android.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WlsApi {
    @GET("/disturbances")
    suspend fun getDisturbances(
        @Query("line") line : List<String>, // Linie (z.B. U6, 31,
        @Query("type") type : List<Int>, // Störungstyp (0 = Bus, 1 = Straßenbahn, 2 = U-Bahn)
        @Query("from") from : String, // Startdatum bzw. nur Störungen ab diesem Datum werden angezeigt (YYYY-MM-DD)
        @Query("to") to : String, // Enddatum bzw. nur Störungen bis zu diesem Datum werden angezeigt (YYYY-MM-DD)
        @Query("order") order : String, // Gibt den Wert an nach dem sortiert werden soll (start, end, type)
        @Query("desc") desc : Boolean, // Gibt an ob Störungen auf- oder absteigend aufgelistet werden sollen
        @Query("active") active : Boolean // Störungen die noch nicht beendet sind, werden noch angezeigt
    ) : Response<List<Disturbance>>

}