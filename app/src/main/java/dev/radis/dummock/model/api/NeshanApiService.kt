package dev.radis.dummock.model.api

import dev.radis.dummock.model.entity.response.DirectionResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NeshanApiService {
    @GET("v3/direction")
    suspend fun getDirection(
        @Query("type") type: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ): Response<DirectionResponseModel>
}