package com.example.Tillora.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Tillora.api.ApiClient
import com.example.Tillora.data.AuthRepository
import com.example.Tillora.data.TokenManager
import com.example.Tillora.models.Customer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository(ApiClient.api)

    private var tokenManager: TokenManager? = null

    // =====================================================
    // CUSTOMER
    // =====================================================

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    // =====================================================
    // TOKEN
    // =====================================================

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    // =====================================================
    // LOADING
    // =====================================================

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // =====================================================
    // ERROR
    // =====================================================

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // =====================================================
    // LOGIN STATUS
    // =====================================================

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // =====================================================
    // OTP / REGISTRATION STATUS
    // =====================================================

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> =
        _isEmailVerified.asStateFlow()

    // =====================================================
    // PENDING REGISTRATION DATA
    // =====================================================

    private var pendingName: String = ""
    private var pendingEmail: String = ""
    private var pendingPhone: String? = null
    private var pendingPassword: String = ""
    private var pendingPasswordConfirmation: String = ""

    // =====================================================
    // INITIALIZE
    // =====================================================

    fun initialize(context: Context) {

        tokenManager = TokenManager(context)

        val savedToken = tokenManager?.getToken()

        if (!savedToken.isNullOrBlank()) {

            _token.value = savedToken
            _isLoggedIn.value = true

            loadCurrentCustomer()
        }
    }

    // =====================================================
    // SEND OTP
    // =====================================================

    fun sendOtp(
        name: String,
        email: String,
        phone: String?,
        password: String,
        passwordConfirmation: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                // Save registration data locally first.
                //
                // This data will be needed after OTP
                // verification when we call /register.

                pendingName = name.trim()
                pendingEmail = email.trim().lowercase()
                pendingPhone = phone?.trim()?.takeIf {
                    it.isNotEmpty()
                }
                pendingPassword = password
                pendingPasswordConfirmation = passwordConfirmation

                // Send OTP to Laravel.

                repository.sendOtp(
                    name = pendingName,
                    email = pendingEmail,
                    phone = pendingPhone,
                    password = pendingPassword,
                    passwordConfirmation = pendingPasswordConfirmation
                )

                // OTP was successfully sent.

                _isEmailVerified.value = false

                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Failed to send verification code."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // =====================================================
    // VERIFY OTP
    // =====================================================

    fun verifyOtp(
        otp: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val cleanOtp = otp.trim()

                if (cleanOtp.length != 6) {

                    _error.value =
                        "Please enter the 6-digit verification code."

                    return@launch
                }

                repository.verifyOtp(
                    email = pendingEmail,
                    otp = cleanOtp
                )

                // Email successfully verified.

                _isEmailVerified.value = true

                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Invalid verification code."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // =====================================================
    // RESEND OTP
    // =====================================================

    fun resendOtp(
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                if (pendingEmail.isBlank()) {

                    _error.value =
                        "Registration session has expired. Please register again."

                    return@launch
                }

                repository.resendOtp(
                    email = pendingEmail
                )

                _isEmailVerified.value = false

                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Failed to send verification code."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // =====================================================
    // COMPLETE REGISTRATION
    // =====================================================

    fun completeRegistration(
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                if (pendingEmail.isBlank()) {

                    _error.value =
                        "Registration session has expired. Please register again."

                    return@launch
                }

                if (!_isEmailVerified.value) {

                    _error.value =
                        "Please verify your email first."

                    return@launch
                }

                // IMPORTANT:
                //
                // Laravel /api/customer/register expects
                // EMAIL ONLY.
                //
                // The name, phone and password were already
                // stored temporarily by sendOtp().

                val response = repository.register(
                    email = pendingEmail
                )

                // Save authentication token.

                response.token?.let { newToken ->

                    tokenManager?.saveToken(newToken)

                    _token.value = newToken
                    _isLoggedIn.value = true
                }

                // Save customer.

                _customer.value = response.customer

                // Registration is complete.

                clearPendingRegistration()

                _isEmailVerified.value = false

                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Registration failed."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val cleanEmail =
                    email.trim().lowercase()

                val response = repository.login(
                    email = cleanEmail,
                    password = password
                )

                response.token?.let { newToken ->

                    tokenManager?.saveToken(newToken)

                    _token.value = newToken
                    _isLoggedIn.value = true
                }

                _customer.value = response.customer

                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Login failed."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // =====================================================
    // LOAD CURRENT CUSTOMER
    // =====================================================

    private fun loadCurrentCustomer() {

        viewModelScope.launch {

            try {

                val response =
                    repository.getCurrentCustomer()

                _customer.value =
                    response.customer

            } catch (e: Exception) {

                e.printStackTrace()

                logoutLocal()
            }
        }
    }

    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    fun updateProfile(
        name: String,
        phone: String?,
        address: String?,
        city: String?,
        country: String?,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val response =
                    repository.updateProfile(
                        name = name.trim(),
                        phone = phone?.trim(),
                        address = address?.trim(),
                        city = city?.trim(),
                        country = country?.trim()
                    )

                _customer.value =
                    response.customer

                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Failed to update profile."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    fun logout(
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                repository.logout()

            } catch (e: Exception) {

                e.printStackTrace()

                // Even if the server logout fails,
                // we still log the user out locally.
            }

            logoutLocal()

            _isLoading.value = false

            onSuccess()
        }
    }

    // =====================================================
    // LOCAL LOGOUT
    // =====================================================

    private fun logoutLocal() {

        tokenManager?.clearToken()

        _customer.value = null
        _token.value = null
        _isLoggedIn.value = false
        _isEmailVerified.value = false
        _error.value = null

        clearPendingRegistration()
    }

    // =====================================================
    // CLEAR PENDING REGISTRATION
    // =====================================================

    private fun clearPendingRegistration() {

        pendingName = ""
        pendingEmail = ""
        pendingPhone = null
        pendingPassword = ""
        pendingPasswordConfirmation = ""
    }

    // =====================================================
    // GET PENDING EMAIL
    // =====================================================

    fun getPendingEmail(): String {
        return pendingEmail
    }

    // =====================================================
    // CHECK EMAIL VERIFICATION
    // =====================================================

    fun isRegistrationEmailVerified(): Boolean {
        return _isEmailVerified.value
    }

    // =====================================================
    // CLEAR ERROR
    // =====================================================

    fun clearError() {
        _error.value = null
    }
}