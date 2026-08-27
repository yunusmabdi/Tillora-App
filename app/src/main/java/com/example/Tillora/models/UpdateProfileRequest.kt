package com.example.Tillora.models

data class UpdateProfileRequest(
    val name: String,
    val phone: String?,
    val address: String?,
    val city: String?,
    val country: String?
)