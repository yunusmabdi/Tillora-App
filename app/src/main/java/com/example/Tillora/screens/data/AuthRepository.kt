package com.example.Tillora.data

import com.example.Tillora.api.TilloraApi
import com.example.Tillora.models.AuthResponse
import com.example.Tillora.models.LoginRequest
import com.example.Tillora.models.MeResponse
import com.example.Tillora.models.RegisterEmailRequest
import com.example.Tillora.models.ResendOtpRequest
import com.example.Tillora.models.SendOtpRequest
import com.example.Tillora.models.SendOtpResponse
import com.example.Tillora.models.VerifyOtpRequest
import com.example.Tillora.models.VerifyOtpResponse
import com.example.Tillora.models.UpdateProfileRequest

class AuthRepository(
    private val api: TilloraApi
) {

    // =====================================================
    // SEND OTP
    // =====================================================

    suspend fun sendOtp(
        name: String,
        email: String,
        phone: String?,
        password: String,
        passwordConfirmation: String
    ): SendOtpResponse {

        return api.sendOtp(
            SendOtpRequest(
                name = name,
                email = email,
                phone = phone,
                password = password,
                password_confirmation = passwordConfirmation
            )
        )
    }

    // =====================================================
    // VERIFY OTP
    // =====================================================

    suspend fun verifyOtp(
        email: String,
        otp: String
    ): VerifyOtpResponse {

        return api.verifyOtp(
            VerifyOtpRequest(
                email = email,
                otp = otp
            )
        )
    }

    // =====================================================
    // RESEND OTP
    // =====================================================

    suspend fun resendOtp(
        email: String
    ): SendOtpResponse {

        return api.resendOtp(
            ResendOtpRequest(
                email = email
            )
        )
    }

    // =====================================================
    // REGISTER
    // =====================================================

    suspend fun register(
        email: String
    ): AuthResponse {

        return api.register(
            RegisterEmailRequest(
                email = email
            )
        )
    }

    // =====================================================
    // LOGIN
    // =====================================================

    suspend fun login(
        email: String,
        password: String
    ): AuthResponse {

        return api.login(
            LoginRequest(
                email = email,
                password = password
            )
        )
    }

    // =====================================================
    // CURRENT CUSTOMER
    // =====================================================

    suspend fun getCurrentCustomer(): MeResponse {
        return api.getCurrentCustomer()
    }

    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    suspend fun updateProfile(
        name: String,
        phone: String?,
        address: String?,
        city: String?,
        country: String?
    ): MeResponse {

        return api.updateProfile(
            UpdateProfileRequest(
                name = name,
                phone = phone,
                address = address,
                city = city,
                country = country
            )
        )
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    suspend fun logout(): AuthResponse {
        return api.logout()
    }
}