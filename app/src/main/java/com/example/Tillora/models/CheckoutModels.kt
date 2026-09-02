package com.example.Tillora.models

// =====================================================
// CHECKOUT ITEM
// =====================================================

data class CheckoutItemRequest(
    val product_id: Int,
    val quantity: Int
)


// =====================================================
// CREATE ORDER REQUEST
// =====================================================
//
// The customer selects a delivery zone from the zones
// returned by Laravel.
//
// Android sends:
// - Cart items
// - Delivery address
// - Selected delivery zone ID
// - Payment option
// - Optional notes
//
// Android does NOT send:
// - Latitude
// - Longitude
// - Delivery fee
//
// Laravel is responsible for calculating the actual
// delivery fee and final order total.
// =====================================================

data class CreateOrderRequest(
    val items: List<CheckoutItemRequest>,
    val delivery_address: String,
    val delivery_zone_id: Int,
    val payment_option: String,
    val notes: String? = null
)


// =====================================================
// CREATE ORDER RESPONSE
// =====================================================

data class CreateOrderResponse(
    val success: Boolean,
    val message: String?,
    val order: Order?
)


// =====================================================
// PAYMENT REQUEST
// =====================================================
//
// Used after the order has been created.
//
// Supported payment methods:
// - mpesa
// - card
// =====================================================

data class ConfirmPaymentRequest(
    val amount_paid: Double,
    val payment_method: String,
    val transaction_reference: String
)


// =====================================================
// PAYMENT RESPONSE
// =====================================================

data class ConfirmPaymentResponse(
    val success: Boolean,
    val message: String?,
    val order: Order?
)