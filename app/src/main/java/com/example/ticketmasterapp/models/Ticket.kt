package com.example.ticketmasterapp.models

data class Ticket(
    val ticketId: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val ticketType: String = "",
    val count: Int = 0,
    val totalPrice: Int = 0,
    val qrBase64: String = "",
    val datePurchased: String = ""
)
