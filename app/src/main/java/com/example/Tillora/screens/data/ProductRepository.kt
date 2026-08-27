package com.example.Tillora.data

import com.example.Tillora.api.ApiClient
import com.example.Tillora.models.Category
import com.example.Tillora.models.Product

class ProductRepository {

    private val api = ApiClient.api

    suspend fun getProducts(): List<Product> {

        val response = api.getProducts()

        if (!response.success) {
            throw Exception("Failed to fetch products")
        }

        return response.products
    }

    suspend fun getCategories(): List<Category> {

        val response = api.getCategories()

        if (!response.success) {
            throw Exception("Failed to fetch categories")
        }

        return response.categories
    }
}