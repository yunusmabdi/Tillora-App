package com.example.Tillora.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Tillora.data.ProductRepository
import com.example.Tillora.models.Category
import com.example.Tillora.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                _products.value = repository.getProducts()
                _categories.value = repository.getCategories()

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Unable to load products and categories"

            } finally {

                _isLoading.value = false
            }
        }
    }
}