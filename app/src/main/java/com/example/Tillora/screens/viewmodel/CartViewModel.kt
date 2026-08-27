package com.example.Tillora.viewmodels

import androidx.lifecycle.ViewModel
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    val cartItems: StateFlow<List<CartItem>> =
        _cartItems.asStateFlow()

    fun addToCart(product: Product) {

        if (product.isOutOfStock) {
            return
        }

        val currentItems = _cartItems.value.toMutableList()

        val existingIndex = currentItems.indexOfFirst {
            it.product.id == product.id
        }

        if (existingIndex >= 0) {

            val existingItem = currentItems[existingIndex]

            currentItems[existingIndex] = existingItem.copy(
                quantity = existingItem.quantity + 1
            )

        } else {

            currentItems.add(
                CartItem(
                    product = product,
                    quantity = 1
                )
            )
        }

        _cartItems.value = currentItems
    }

    fun increaseQuantity(productId: Int) {

        val currentItems = _cartItems.value.toMutableList()

        val index = currentItems.indexOfFirst {
            it.product.id == productId
        }

        if (index >= 0) {

            val item = currentItems[index]

            currentItems[index] = item.copy(
                quantity = item.quantity + 1
            )

            _cartItems.value = currentItems
        }
    }

    fun decreaseQuantity(productId: Int) {

        val currentItems = _cartItems.value.toMutableList()

        val index = currentItems.indexOfFirst {
            it.product.id == productId
        }

        if (index >= 0) {

            val item = currentItems[index]

            if (item.quantity > 1) {

                currentItems[index] = item.copy(
                    quantity = item.quantity - 1
                )

            } else {

                currentItems.removeAt(index)
            }

            _cartItems.value = currentItems
        }
    }

    fun removeFromCart(productId: Int) {

        _cartItems.value = _cartItems.value.filter {
            it.product.id != productId
        }
    }

    fun clearCart() {

        _cartItems.value = emptyList()
    }

    val itemCount: Int
        get() = _cartItems.value.sumOf {
            it.quantity
        }

    val subtotal: Double
        get() = _cartItems.value.sumOf {
            it.totalPrice
        }
}