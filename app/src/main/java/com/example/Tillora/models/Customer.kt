package com.example.Tillora.models

data class Customer(
    val id: Int,
    val customer_code: String?,
    val name: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val city: String?,
    val country: String?,
    val is_active: Boolean
)