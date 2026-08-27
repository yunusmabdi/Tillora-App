package com.example.Tillora.api

import com.example.Tillora.models.Category
import com.example.Tillora.models.LoginRequest
import com.example.Tillora.models.MeResponse
import com.example.Tillora.models.Product
import com.example.Tillora.models.RegisterEmailRequest
import com.example.Tillora.models.ResendOtpRequest
import com.example.Tillora.models.SendOtpRequest
import com.example.Tillora.models.SendOtpResponse
import com.example.Tillora.models.VerifyOtpRequest
import com.example.Tillora.models.VerifyOtpResponse
import com.example.Tillora.models.AuthResponse
import com.example.Tillora.models.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

data class ProductsResponse(
    val success: Boolean,
    val products: List<Product>
)

data class CategoriesResponse(
    val success: Boolean,
    val categories: List<Category>
)

interface TilloraApi {

    // =====================================================
    // PRODUCTS
    // =====================================================

    @GET("api/products")
    suspend fun getProducts(): ProductsResponse

    // =====================================================
    // CATEGORIES
    // =====================================================

    @GET("api/categories")
    suspend fun getCategories(): CategoriesResponse

    // =====================================================
    // CUSTOMER AUTHENTICATION
    // =====================================================

    @POST("api/customer/send-otp")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): SendOtpResponse

    @POST("api/customer/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): VerifyOtpResponse

    @POST("api/customer/resend-otp")
    suspend fun resendOtp(
        @Body request: ResendOtpRequest
    ): SendOtpResponse

    @POST("api/customer/register")
    suspend fun register(
        @Body request: RegisterEmailRequest
    ): AuthResponse

    @POST("api/customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    // =====================================================
    // CUSTOMER PROFILE
    // =====================================================

    @GET("api/customer/me")
    suspend fun getCurrentCustomer(): MeResponse

    @PUT("api/customer/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): MeResponse

    // =====================================================
    // LOGOUT
    // =====================================================

    @POST("api/customer/logout")
    suspend fun logout(): AuthResponse
}