package com.example.Tillora.models

data class SendOtpRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val password: String,
    val password_confirmation: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ResendOtpRequest(
    val email: String
)

data class SendOtpResponse(
    val message: String
)

data class VerifyOtpResponse(
    val message: String
)