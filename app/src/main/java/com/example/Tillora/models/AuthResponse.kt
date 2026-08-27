package com.example.Tillora.models

data class AuthResponse(
    val message: String,
    val customer: Customer?,
    val token: String?
)