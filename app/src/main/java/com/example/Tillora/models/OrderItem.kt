package com.example.Tillora.models

import com.google.gson.annotations.SerializedName

data class OrderItem(
    val id: Int,

    @SerializedName("sale_id")
    val saleId: Int,

    @SerializedName("product_id")
    val productId: Int,

    val quantity: Int,

    @SerializedName("unit_price")
    val unitPrice: Double,

    @SerializedName("original_price")
    val originalPrice: Double,

    @SerializedName("discount_amount")
    val discountAmount: Double,

    @SerializedName("line_total")
    val lineTotal: Double,

    val product: Product? = null
)