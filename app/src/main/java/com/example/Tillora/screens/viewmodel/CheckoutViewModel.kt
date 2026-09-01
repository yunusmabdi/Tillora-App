package com.example.Tillora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Tillora.api.ApiClient
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.CheckoutItemRequest
import com.example.Tillora.models.ConfirmPaymentRequest
import com.example.Tillora.models.CreateOrderRequest
import com.example.Tillora.models.DeliveryCalculationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// =========================================================
// DELIVERY RESULT
// =========================================================

data class DeliveryResult(
    val deliverable: Boolean = false,
    val distance: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val zoneId: Int? = null,
    val zoneName: String? = null
)

// =========================================================
// CHECKOUT STATE
// =========================================================

data class CheckoutState(

    val deliveryAddress: String = "",

    val latitude: Double? = null,

    val longitude: Double? = null,

    val isCalculatingDelivery: Boolean = false,

    val deliveryResult: DeliveryResult? = null,

    val notes: String = "",

    val paymentOption: String = "full",

    val isSubmittingOrder: Boolean = false,

    val createdOrderId: Int? = null,

    val isConfirmingPayment: Boolean = false,

    val error: String? = null,

    val successMessage: String? = null
)

// =========================================================
// CHECKOUT VIEW MODEL
// =========================================================

class CheckoutViewModel : ViewModel() {

    private val _state =
        MutableStateFlow(CheckoutState())

    val state: StateFlow<CheckoutState> =
        _state.asStateFlow()

    // =====================================================
    // ADDRESS
    // =====================================================

    fun setDeliveryAddress(
        address: String
    ) {

        _state.value =
            _state.value.copy(
                deliveryAddress = address,
                deliveryResult = null,
                error = null
            )
    }

    // =====================================================
    // CUSTOMER LOCATION
    // =====================================================

    fun setCustomerLocation(
        latitude: Double,
        longitude: Double
    ) {

        _state.value =
            _state.value.copy(
                latitude = latitude,
                longitude = longitude,
                error = null
            )
    }

    // =====================================================
    // CALCULATE DELIVERY
    // =====================================================

    fun calculateDelivery() {

        val currentState =
            _state.value

        val latitude =
            currentState.latitude

        val longitude =
            currentState.longitude

        if (latitude == null || longitude == null) {

            _state.value =
                currentState.copy(
                    error =
                        "Please provide your delivery location."
                )

            return
        }

        viewModelScope.launch {

            _state.value =
                _state.value.copy(
                    isCalculatingDelivery = true,
                    error = null
                )

            try {

                val response =
                    ApiClient.api.calculateDelivery(
                        DeliveryCalculationRequest(
                            latitude = latitude,
                            longitude = longitude
                        )
                    )

                if (response.deliverable == true) {

                    _state.value =
                        _state.value.copy(

                            isCalculatingDelivery =
                                false,

                            deliveryResult =
                                DeliveryResult(

                                    deliverable = true,

                                    distance =
                                        response.distance
                                            ?: 0.0,

                                    deliveryFee =
                                        response.delivery_fee
                                            ?: 0.0,

                                    zoneId =
                                        response.zone?.id,

                                    zoneName =
                                        response.zone?.name
                                ),

                            error = null
                        )

                } else {

                    _state.value =
                        _state.value.copy(

                            isCalculatingDelivery =
                                false,

                            deliveryResult =
                                DeliveryResult(
                                    deliverable = false
                                ),

                            error =
                                response.message
                                    ?: "This location is outside our delivery area."
                        )
                }

            } catch (e: Exception) {

                _state.value =
                    _state.value.copy(

                        isCalculatingDelivery =
                            false,

                        error =
                            e.message
                                ?: "Unable to calculate delivery."
                    )
            }
        }
    }

    // =====================================================
    // NOTES
    // =====================================================

    fun setNotes(
        notes: String
    ) {

        _state.value =
            _state.value.copy(
                notes = notes
            )
    }

    // =====================================================
    // PAYMENT OPTION
    // =====================================================

    fun setPaymentOption(
        option: String
    ) {

        if (
            option != "full" &&
            option != "advance"
        ) {
            return
        }

        _state.value =
            _state.value.copy(
                paymentOption = option,
                error = null
            )
    }

    // =====================================================
    // CREATE ORDER
    // =====================================================

    fun createOrder(
        cartItems: List<CartItem>,
        onSuccess: (Int) -> Unit
    ) {

        val currentState =
            _state.value

        if (cartItems.isEmpty()) {

            _state.value =
                currentState.copy(
                    error = "Your cart is empty."
                )

            return
        }

        if (
            currentState.deliveryAddress.isBlank()
        ) {

            _state.value =
                currentState.copy(
                    error =
                        "Please enter your delivery address."
                )

            return
        }

        val latitude =
            currentState.latitude

        val longitude =
            currentState.longitude

        if (
            latitude == null ||
            longitude == null
        ) {

            _state.value =
                currentState.copy(
                    error =
                        "Please provide your delivery location."
                )

            return
        }

        val delivery =
            currentState.deliveryResult

        if (
            delivery == null ||
            !delivery.deliverable
        ) {

            _state.value =
                currentState.copy(
                    error =
                        "Please calculate your delivery fee first."
                )

            return
        }

        val zoneId =
            delivery.zoneId

        if (zoneId == null) {

            _state.value =
                currentState.copy(
                    error =
                        "No delivery zone was found."
                )

            return
        }

        viewModelScope.launch {

            _state.value =
                _state.value.copy(
                    isSubmittingOrder = true,
                    error = null,
                    successMessage = null
                )

            try {

                val request =
                    CreateOrderRequest(

                        items =
                            cartItems.map { cartItem ->

                                CheckoutItemRequest(
                                    product_id =
                                        cartItem.product.id,

                                    quantity =
                                        cartItem.quantity
                                )
                            },

                        delivery_address =
                            currentState
                                .deliveryAddress
                                .trim(),

                        latitude =
                            latitude,

                        longitude =
                            longitude,

                        delivery_zone_id =
                            zoneId,

                        delivery_fee =
                            delivery.deliveryFee,

                        payment_option =
                            currentState
                                .paymentOption,

                        notes =
                            currentState
                                .notes
                                .trim()
                                .ifBlank {
                                    null
                                }
                    )

                val response =
                    ApiClient.api.createOrder(
                        request
                    )

                if (
                    response.success &&
                    response.order != null
                ) {

                    val orderId =
                        response.order.id

                    _state.value =
                        _state.value.copy(

                            isSubmittingOrder =
                                false,

                            createdOrderId =
                                orderId,

                            successMessage =
                                "Order created. Proceed to payment.",

                            error = null
                        )

                    onSuccess(orderId)

                } else {

                    _state.value =
                        _state.value.copy(

                            isSubmittingOrder =
                                false,

                            error =
                                response.message
                                    ?: "Unable to create order."
                        )
                }

            } catch (e: Exception) {

                _state.value =
                    _state.value.copy(

                        isSubmittingOrder =
                            false,

                        error =
                            e.message
                                ?: "Unable to create order."
                    )
            }
        }
    }

    // =====================================================
    // CONFIRM PAYMENT
    // =====================================================

    fun confirmPayment(
        orderId: Int,
        amountPaid: Double,
        paymentMethod: String,
        transactionReference: String,
        onSuccess: () -> Unit
    ) {

        if (amountPaid <= 0) {

            _state.value =
                _state.value.copy(
                    error =
                        "Payment amount must be greater than zero."
                )

            return
        }

        if (paymentMethod.isBlank()) {

            _state.value =
                _state.value.copy(
                    error =
                        "Please select a payment method."
                )

            return
        }

        if (
            transactionReference.isBlank()
        ) {

            _state.value =
                _state.value.copy(
                    error =
                        "Payment transaction reference is required."
                )

            return
        }

        viewModelScope.launch {

            _state.value =
                _state.value.copy(
                    isConfirmingPayment = true,
                    error = null,
                    successMessage = null
                )

            try {

                val request =
                    ConfirmPaymentRequest(

                        amount_paid =
                            amountPaid,

                        payment_method =
                            paymentMethod.trim(),

                        transaction_reference =
                            transactionReference.trim()
                    )

                val response =
                    ApiClient.api.confirmPayment(
                        id = orderId,
                        request = request
                    )

                if (response.success) {

                    _state.value =
                        _state.value.copy(

                            isConfirmingPayment =
                                false,

                            successMessage =
                                response.message
                                    ?: "Payment confirmed successfully.",

                            error = null
                        )

                    onSuccess()

                } else {

                    _state.value =
                        _state.value.copy(

                            isConfirmingPayment =
                                false,

                            error =
                                response.message
                                    ?: "Payment could not be confirmed."
                        )
                }

            } catch (e: Exception) {

                _state.value =
                    _state.value.copy(

                        isConfirmingPayment =
                            false,

                        error =
                            e.message
                                ?: "Payment could not be confirmed."
                    )
            }
        }
    }

    // =====================================================
    // CLEAR ERROR
    // =====================================================

    fun clearError() {

        _state.value =
            _state.value.copy(
                error = null
            )
    }

    // =====================================================
    // CLEAR SUCCESS
    // =====================================================

    fun clearSuccess() {

        _state.value =
            _state.value.copy(
                successMessage = null
            )
    }
}