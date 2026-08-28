package com.example.Tillora.api

import com.example.Tillora.models.AuthResponse
import com.example.Tillora.models.Category
import com.example.Tillora.models.LoginRequest
import com.example.Tillora.models.MeResponse
import com.example.Tillora.models.OrderResponse
import com.example.Tillora.models.Product
import com.example.Tillora.models.RegisterEmailRequest
import com.example.Tillora.models.ResendOtpRequest
import com.example.Tillora.models.SendOtpRequest
import com.example.Tillora.models.SendOtpResponse
import com.example.Tillora.models.SingleOrderResponse
import com.example.Tillora.models.UpdateProfileRequest
import com.example.Tillora.models.VerifyOtpRequest
import com.example.Tillora.models.VerifyOtpResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.example.Tillora.models.Order
data class ProductsResponse(
    val success: Boolean,
    val products: List<Product>
)

data class CategoriesResponse(
    val success: Boolean,
    val categories: List<Category>
)

interface TilloraApi {

    @GET("api/products")
    suspend fun getProducts(): ProductsResponse

    @GET("api/categories")
    suspend fun getCategories(): CategoriesResponse

    // =====================================================
    // ORDERS
    // =====================================================

    @GET("api/customer/orders")
    suspend fun getOrders(): OrderResponse

    @GET("api/customer/orders/{id}")
    suspend fun getOrder(
        @Path("id") id: Int
    ): SingleOrderResponse

    // =====================================================
    // AUTHENTICATION
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
    // PROFILE
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