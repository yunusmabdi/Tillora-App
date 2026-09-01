package com.example.Tillora.models

// =====================================================
// CHECKOUT ITEM
// =====================================================

data class CheckoutItemRequest(
    val product_id: Int,
    val quantity: Int
)

// =====================================================
// DELIVERY CALCULATION REQUEST
// =====================================================
//
// Customer coordinates are sent to Laravel.
// Laravel determines:
// - Store
// - Distance
// - Delivery zone
// - Delivery fee
//
// Android does NOT determine the zone.
// =====================================================

data class DeliveryCalculationRequest(
    val latitude: Double,
    val longitude: Double
)

// =====================================================
// DELIVERY CALCULATION RESPONSE
// =====================================================

data class DeliveryCalculationResponse(
    val success: Boolean,
    val message: String?,
    val deliverable: Boolean?,
    val distance: Double?,
    val delivery_fee: Double?,
    val zone: DeliveryZone?,
    val store: DeliveryStore?
)

// =====================================================
// DELIVERY STORE
// =====================================================

data class DeliveryStore(
    val id: Int,
    val name: String
)

// =====================================================
// CREATE ORDER REQUEST
// =====================================================
//
// delivery_zone_id and delivery_fee are returned by
// Laravel's delivery calculation and sent when creating
// the order.
//
// Latitude and longitude are also retained so Laravel
// can validate the delivery location again.
// =====================================================

data class CreateOrderRequest(
    val items: List<CheckoutItemRequest>,
    val delivery_address: String,
    val latitude: Double,
    val longitude: Double,
    val delivery_zone_id: Int,
    val delivery_fee: Double,
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