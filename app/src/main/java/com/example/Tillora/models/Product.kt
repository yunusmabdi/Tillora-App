package com.example.Tillora.models

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,

    @SerializedName("category_id")
    val categoryId: Int,

    val sku: String,
    val name: String,
    val image: String?,

    @SerializedName("selling_price")
    val sellingPrice: Double,

    @SerializedName("stock_quantity")
    val stockQuantity: Double,

    @SerializedName("minimum_stock")
    val minimumStock: Double,

    val unit: String,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("discount_type")
    val discountType: String,

    @SerializedName("discount_value")
    val discountValue: Double,

    @SerializedName("discount_active")
    val discountActive: Boolean
) {

    val isOutOfStock: Boolean
        get() = stockQuantity <= 0

    val hasDiscount: Boolean
        get() = discountActive && discountType != "none"

    val discountedPrice: Double
        get() {
            if (!hasDiscount) {
                return sellingPrice
            }

            return when (discountType) {
                "percentage" -> {
                    (sellingPrice - (sellingPrice * discountValue / 100))
                        .coerceAtLeast(0.0)
                }

                "fixed" -> {
                    (sellingPrice - discountValue)
                        .coerceAtLeast(0.0)
                }

                else -> sellingPrice
            }
        }
}