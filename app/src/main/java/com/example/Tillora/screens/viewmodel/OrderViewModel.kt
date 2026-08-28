package com.example.Tillora.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Tillora.models.Order
import com.example.Tillora.screens.data.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val repository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadOrders() {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            repository.getOrders()
                .onSuccess { orders ->
                    _orders.value = orders
                }
                .onFailure { exception ->
                    _error.value =
                        exception.message ?: "Failed to load orders."
                }

            _isLoading.value = false
        }
    }

    fun getOrder(
        id: Int,
        onResult: (Order?) -> Unit
    ) {

        viewModelScope.launch {

            repository.getOrder(id)
                .onSuccess { order ->
                    onResult(order)
                }
                .onFailure {
                    onResult(null)
                }
        }
    }
}