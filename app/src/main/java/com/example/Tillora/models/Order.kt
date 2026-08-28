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

    @SerializedName("payment_method")
    val paymentMethod: String?,

    val subtotal: Double,

    @SerializedName("discount_amount")
    val discountAmount: Double,

    val discount: Double,

    val tax: Double,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("amount_paid")
    val amountPaid: Double,

    @SerializedName("change_amount")
    val changeAmount: Double,

    val notes: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    val items: List<OrderItem> = emptyList()
)