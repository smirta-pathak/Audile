package com.mrsep.musicrecognizer.core.domain.concerts

data class Concert(
    val id: String,
    val name: String,
    val date: String,
    val venueName: String,
    val city: String,
    val country: String,
    val imageUrl: String?,
    val ticketUrl: String,
)
