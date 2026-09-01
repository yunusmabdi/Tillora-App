package com.example.Tillora.models

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,

    @SerializedName("invoice_number")
    val invoiceNumber: String,

    @SerializedName("customer_id")
    val customerId: Int?,

    @SerializedName("sale_date")
    val saleDate: String,

    val status: String,

    @SerializedName("payment_status")
    val paymentStatus: String,

    @SerializedName("fulfillment_status")
    val fulfillmentStatus: String,

    @SerializedName("payment_method")
    val paymentMethod: String?,

    @SerializedName("transaction_reference")
    val transactionReference: String?,

    val subtotal: Double,

    @SerializedName("discount_amount")
    val discountAmount: Double = 0.0,

    val discount: Double,

    val tax: Double,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("amount_paid")
    val amountPaid: Double,

    @SerializedName("advance_amount")
    val advanceAmount: Double,

    @SerializedName("balance_amount")
    val balanceAmount: Double,

    @SerializedName("change_amount")
    val changeAmount: Double,

    @SerializedName("delivery_address")
    val deliveryAddress: String?,

    @SerializedName("delivery_fee")
    val deliveryFee: Double,

    @SerializedName("delivery_zone_id")
    val deliveryZoneId: Int?,

    val notes: String?,

    @SerializedName("cancellation_reason")
    val cancellationReason: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    val items: List<OrderItem> = emptyList()
)