package com.example.Tillora.models

data class OrderResponse(
    val success: Boolean,
    val orders: List<Order>
)