package com.example.Tillora.screens.data

import com.example.Tillora.api.ApiClient
import com.example.Tillora.models.Order

class OrderRepository {

    private val api = ApiClient.api

    suspend fun getOrders(): Result<List<Order>> {

        return try {

            val response = api.getOrders()

            if (response.success) {
                Result.success(response.orders)
            } else {
                Result.failure(
                    Exception("Failed to load orders.")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getOrder(id: Int): Result<Order> {

        return try {

            val response = api.getOrder(id)

            if (response.success) {
                Result.success(response.order)
            } else {
                Result.failure(
                    Exception("Order not found.")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}