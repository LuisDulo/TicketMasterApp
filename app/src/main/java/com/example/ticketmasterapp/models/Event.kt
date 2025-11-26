package com.example.ticketmasterapp.models

data class Event(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val date: String = "",
    val location: String = "",
    val description: String = "",
    val regularPrice: Double = 0.0,
    val vipPrice: Double = 0.0,
    val vvipPrice: Double = 0.0,
    val imageUrl: String = ""
)


