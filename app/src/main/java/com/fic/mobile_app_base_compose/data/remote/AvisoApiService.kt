package com.fic.mobile_app_base_compose.data.remote

import retrofit2.http.GET

interface AvisoApiService {
    @GET("posts")
    suspend fun obtenerAvisosExternos(): List<AvisoApiDto>
}
