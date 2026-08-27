package com.example.Tillora.components

import com.example.Tillora.models.Product

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val unitPrice: Double
        get() = product.discountedPrice

    val totalPrice: Double
        get() = unitPrice * quantity
}