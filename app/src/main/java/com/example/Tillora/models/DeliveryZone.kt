package com.example.Tillora.models

data class DeliveryZone(
    val id: Int,
    val name: String,
    val fee: Double = 0.0,
    val description: String? = null
)

data class DeliveryZonesResponse(
    val success: Boolean,
    val deliveryZones: List<DeliveryZone> = emptyList(),
    val message: String? = null
)