package com.example.Tillora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Tillora.api.ApiClient
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.CheckoutItemRequest
import com.example.Tillora.models.ConfirmPaymentRequest
import com.example.Tillora.models.CreateOrderRequest
import com.example.Tillora.models.DeliveryZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


// =====================================================
// DELIVERY ZONE
// =====================================================

data class DeliveryResult(
    val zoneId: Int? = null,
    val zoneName: String? = null,
    val deliveryFee: Double = 0.0
)


// =====================================================
// CHECKOUT STATE
// =====================================================

data class CheckoutState(
    val deliveryAddress: String = "",

    val deliveryZones: List<DeliveryZone> = emptyList(),
    val selectedDeliveryZone: DeliveryZone? = null,
    val isLoadingDeliveryZones: Boolean = false,

    val notes: String = "",

    val paymentOption: String = "full",

    val isSubmittingOrder: Boolean = false,

    val createdOrderId: Int? = null,

    val isConfirmingPayment: Boolean = false,

    val error: String? = null,

    val successMessage: String? = null
)


// =====================================================
// CHECKOUT VIEW MODEL
// =====================================================

class CheckoutViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _state = MutableStateFlow(CheckoutState())

    val state: StateFlow<CheckoutState> =
        _state.asStateFlow()


    // =================================================
    // DELIVERY ADDRESS
    // =================================================

    fun setDeliveryAddress(address: String) {
        _state.value = _state.value.copy(
            deliveryAddress = address,
            error = null
        )
    }


    // =================================================
    // LOAD DELIVERY ZONES
    // =================================================

    fun loadDeliveryZones() {

        if (_state.value.isLoadingDeliveryZones) {
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoadingDeliveryZones = true,
                error = null
            )

            try {

                val response =
                    api.getDeliveryZones()

                if (response.success) {

                    _state.value = _state.value.copy(
                        deliveryZones =
                            response.delivery_zones,
                        isLoadingDeliveryZones = false
                    )

                } else {

                    _state.value = _state.value.copy(
                        isLoadingDeliveryZones = false,
                        error =
                            response.message
                                ?: "Unable to load delivery zones."
                    )
                }

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoadingDeliveryZones = false,
                    error =
                        e.message
                            ?: "Unable to load delivery zones."
                )
            }
        }
    }


    // =================================================
    // SELECT DELIVERY ZONE
    // =================================================

    fun selectDeliveryZone(zone: DeliveryZone) {

        _state.value = _state.value.copy(
            selectedDeliveryZone = zone,
            error = null
        )
    }


    // =================================================
    // DELIVERY RESULT
    // =================================================

    fun getDeliveryResult(): DeliveryResult? {

        val zone =
            _state.value.selectedDeliveryZone
                ?: return null

        return DeliveryResult(
            zoneId = zone.id,
            zoneName = zone.name,
            deliveryFee = zone.fee
        )
    }


    // =================================================
    // NOTES
    // =================================================

    fun setNotes(notes: String) {

        _state.value = _state.value.copy(
            notes = notes
        )
    }


    // =================================================
    // PAYMENT OPTION
    // =================================================

    fun setPaymentOption(option: String) {

        if (
            option != "full" &&
            option != "advance"
        ) {
            return
        }

        _state.value = _state.value.copy(
            paymentOption = option,
            error = null
        )
    }


    // =================================================
    // CREATE ORDER
    // =================================================

    fun createOrder(
        cartItems: List<CartItem>,
        onSuccess: (Int, Double) -> Unit
    ) {

        val currentState = _state.value

        // ---------------------------------------------
        // VALIDATE CART
        // ---------------------------------------------

        if (cartItems.isEmpty()) {

            _state.value = currentState.copy(
                error = "Your cart is empty."
            )

            return
        }

        // ---------------------------------------------
        // VALIDATE ADDRESS
        // ---------------------------------------------

        if (
            currentState.deliveryAddress
                .isBlank()
        ) {

            _state.value = currentState.copy(
                error =
                    "A delivery address is required."
            )

            return
        }

        // ---------------------------------------------
        // VALIDATE DELIVERY ZONE
        // ---------------------------------------------

        val zone =
            currentState.selectedDeliveryZone

        if (zone == null) {

            _state.value = currentState.copy(
                error =
                    "Please select a delivery zone."
            )

            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isSubmittingOrder = true,
                error = null
            )

            try {

                // -------------------------------------
                // CREATE REQUEST
                // -------------------------------------

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

                        delivery_zone_id =
                            zone.id,

                        payment_option =
                            currentState
                                .paymentOption,

                        notes =
                            currentState.notes
                                .trim()
                                .ifBlank {
                                    null
                                }
                    )

                // -------------------------------------
                // SEND TO LARAVEL
                // -------------------------------------

                val response =
                    api.createOrder(request)

                if (
                    response.success &&
                    response.order != null
                ) {

                    val order =
                        response.order

                    val requiredPayment =
                        if (
                            currentState
                                .paymentOption ==
                            "full"
                        ) {
                            order.totalAmount
                        } else {
                            order.totalAmount * 0.50
                        }

                    _state.value =
                        _state.value.copy(
                            isSubmittingOrder = false,
                            createdOrderId = order.id,
                            successMessage =
                                response.message
                                    ?: "Order created successfully."
                        )

                    onSuccess(
                        order.id,
                        requiredPayment
                    )

                } else {

                    _state.value =
                        _state.value.copy(
                            isSubmittingOrder = false,
                            error =
                                response.message
                                    ?: "Unable to create order."
                        )
                }

            } catch (e: Exception) {

                _state.value =
                    _state.value.copy(
                        isSubmittingOrder = false,
                        error =
                            e.message
                                ?: "Unable to create order."
                    )
            }
        }
    }


    // =================================================
    // CONFIRM PAYMENT
    // =================================================

    fun confirmPayment(
        orderId: Int,
        amountPaid: Double,
        paymentMethod: String,
        transactionReference: String,
        onSuccess: () -> Unit
    ) {

        if (amountPaid <= 0) {

            _state.value = _state.value.copy(
                error = "Payment amount must be greater than zero."
            )

            return
        }

        if (paymentMethod.isBlank()) {

            _state.value = _state.value.copy(
                error = "Please select a payment method."
            )

            return
        }

        if (transactionReference.isBlank()) {

            _state.value = _state.value.copy(
                error =
                    "Transaction reference is required."
            )

            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isConfirmingPayment = true,
                error = null
            )

            try {

                val request =
                    ConfirmPaymentRequest(
                        amount_paid = amountPaid,
                        payment_method = paymentMethod,
                        transaction_reference =
                            transactionReference.trim()
                    )

                val response =
                    api.confirmPayment(
                        id = orderId,
                        request = request
                    )

                if (response.success) {

                    _state.value =
                        _state.value.copy(
                            isConfirmingPayment = false,
                            successMessage =
                                response.message
                                    ?: "Payment confirmed successfully."
                        )

                    onSuccess()

                } else {

                    _state.value =
                        _state.value.copy(
                            isConfirmingPayment = false,
                            error =
                                response.message
                                    ?: "Payment confirmation failed."
                        )
                }

            } catch (e: Exception) {

                _state.value =
                    _state.value.copy(
                        isConfirmingPayment = false,
                        error =
                            e.message
                                ?: "Payment confirmation failed."
                    )
            }
        }
    }


    // =================================================
    // CLEAR ERROR
    // =================================================

    fun clearError() {

        _state.value = _state.value.copy(
            error = null
        )
    }


    // =================================================
    // CLEAR SUCCESS
    // =================================================

    fun clearSuccess() {

        _state.value = _state.value.copy(
            successMessage = null
        )
    }


    // =================================================
    // RESET CHECKOUT
    // =================================================

    fun resetCheckout() {

        _state.value = CheckoutState()
    }
}