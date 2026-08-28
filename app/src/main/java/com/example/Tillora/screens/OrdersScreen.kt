package com.example.Tillora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Tillora.components.OrderCard
import com.example.Tillora.models.Order
import com.example.Tillora.models.OrderItem
import com.example.Tillora.ui.theme.TilloraBackground
import com.example.Tillora.ui.theme.TilloraNavy
import com.example.Tillora.ui.theme.TilloraTextPrimary
import com.example.Tillora.ui.theme.TilloraTextSecondary

@Composable
fun OrdersScreen(
    onOrderClick: (Order) -> Unit = {}
) {

    // =========================================================
    // TEMPORARY MOCK ORDERS
    // =========================================================

    val orders = remember {

        listOf(

            Order(
                id = 1,
                invoiceNumber = "INV-10245",
                customerId = 1,
                saleDate = "Aug 27, 2026",
                status = "delivered",
                paymentMethod = "M-Pesa",
                subtotal = 4500.0,
                discountAmount = 0.0,
                discount = 0.0,
                tax = 720.0,
                totalAmount = 5220.0,
                amountPaid = 5220.0,
                changeAmount = 0.0,
                notes = null,
                createdAt = "",
                updatedAt = "",
                items = listOf(
                    OrderItem(
                        id = 1,
                        saleId = 1,
                        productId = 1,
                        quantity = 2,
                        unitPrice = 2000.0,
                        originalPrice = 2000.0,
                        discountAmount = 0.0,
                        lineTotal = 4000.0
                    ),
                    OrderItem(
                        id = 2,
                        saleId = 1,
                        productId = 2,
                        quantity = 1,
                        unitPrice = 500.0,
                        originalPrice = 500.0,
                        discountAmount = 0.0,
                        lineTotal = 500.0
                    )
                )
            ),

            Order(
                id = 2,
                invoiceNumber = "INV-10244",
                customerId = 1,
                saleDate = "Aug 25, 2026",
                status = "processing",
                paymentMethod = "M-Pesa",
                subtotal = 2300.0,
                discountAmount = 0.0,
                discount = 0.0,
                tax = 368.0,
                totalAmount = 2668.0,
                amountPaid = 2668.0,
                changeAmount = 0.0,
                notes = null,
                createdAt = "",
                updatedAt = "",
                items = listOf(
                    OrderItem(
                        id = 3,
                        saleId = 2,
                        productId = 3,
                        quantity = 2,
                        unitPrice = 1150.0,
                        originalPrice = 1150.0,
                        discountAmount = 0.0,
                        lineTotal = 2300.0
                    )
                )
            ),

            Order(
                id = 3,
                invoiceNumber = "INV-10243",
                customerId = 1,
                saleDate = "Aug 22, 2026",
                status = "shipped",
                paymentMethod = "M-Pesa",
                subtotal = 3150.0,
                discountAmount = 150.0,
                discount = 150.0,
                tax = 480.0,
                totalAmount = 3480.0,
                amountPaid = 3480.0,
                changeAmount = 0.0,
                notes = null,
                createdAt = "",
                updatedAt = "",
                items = listOf(
                    OrderItem(
                        id = 4,
                        saleId = 3,
                        productId = 4,
                        quantity = 1,
                        unitPrice = 3150.0,
                        originalPrice = 3300.0,
                        discountAmount = 150.0,
                        lineTotal = 3000.0
                    )
                )
            )
        )
    }

    // =========================================================
    // SELECTED FILTER
    // =========================================================

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    // =========================================================
    // FILTER ORDERS
    // =========================================================

    val filteredOrders = when (selectedFilter) {

        "Processing" ->
            orders.filter {
                it.status.equals("processing", ignoreCase = true)
            }

        "Shipped" ->
            orders.filter {
                it.status.equals("shipped", ignoreCase = true)
            }

        "Delivered" ->
            orders.filter {
                it.status.equals("delivered", ignoreCase = true)
            }

        else -> orders
    }

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TilloraBackground)
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp
                )
        ) {

            Text(
                text = "My Orders",
                color = TilloraTextPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Track and manage your orders",
                color = TilloraTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // FILTERS
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Processing",
                "Shipped",
                "Delivered"
            ).forEach { filter ->

                val isSelected = selectedFilter == filter

                Text(
                    text = filter,

                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(50.dp)
                        )
                        .background(
                            if (isSelected) {
                                TilloraNavy
                            } else {
                                Color.White
                            }
                        )
                        .clickable {
                            selectedFilter = filter
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 9.dp
                        ),

                    color = if (isSelected) {
                        Color.White
                    } else {
                        TilloraTextSecondary
                    },

                    fontWeight = if (isSelected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },

                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // =====================================================
        // ORDERS
        // =====================================================

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 20.dp
            ),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(
                items = filteredOrders,
                key = { it.id }
            ) { order ->

                OrderCard(
                    order = order,
                    onClick = {
                        onOrderClick(order)
                    }
                )
            }
        }

        // =====================================================
        // BOTTOM NAVIGATION SAFETY
        // =====================================================

        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .height(4.dp)
        )
    }
}