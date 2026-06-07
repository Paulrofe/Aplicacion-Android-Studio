package com.fic.mobile_app_base_compose.data.remote

data class AvisoApiDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
