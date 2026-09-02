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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Tillora.components.OrderCard
import com.example.Tillora.models.Order
import com.example.Tillora.screens.viewmodel.OrderViewModel
import com.example.Tillora.ui.theme.TilloraBackground
import com.example.Tillora.ui.theme.TilloraNavy
import com.example.Tillora.ui.theme.TilloraTextPrimary
import com.example.Tillora.ui.theme.TilloraTextSecondary

@Composable
fun OrdersScreen(
    onOrderClick: (Order) -> Unit = {},
    viewModel: OrderViewModel = viewModel()
) {

    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    // =====================================================
    // FILTER ORDERS
    // =====================================================

    val filteredOrders = when (selectedFilter) {

        "Processing" -> {
            orders
                .filter { order ->
                    order.fulfillmentStatus.equals("order placed", ignoreCase = true) ||
                            order.fulfillmentStatus.equals("processing", ignoreCase = true) ||
                            order.fulfillmentStatus.equals("ready", ignoreCase = true)
                }
                .sortedByDescending { it.createdAt }
        }

        "Shipped" -> {
            orders
                .filter { order ->
                    order.fulfillmentStatus.equals(
                        "out for delivery",
                        ignoreCase = true
                    )
                }
                .sortedByDescending { it.createdAt }
        }

        "Delivered" -> {
            orders
                .filter { order ->
                    order.fulfillmentStatus.equals(
                        "delivered",
                        ignoreCase = true
                    )
                }
                .sortedByDescending { it.createdAt }
        }

        "Cancelled" -> {
            orders
                .filter { order ->
                    order.fulfillmentStatus.equals(
                        "cancelled",
                        ignoreCase = true
                    )
                }
                .sortedByDescending { it.createdAt }
        }

        "All" -> {
            orders.sortedByDescending { it.createdAt }
        }

        else -> {
            orders.sortedByDescending { it.createdAt }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TilloraBackground)
    ) {

        // =================================================
        // HEADER
        // =================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 35.dp,
                    bottom = 8.dp
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

        // =================================================
        // ORDER FILTERS
        // =================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Processing",
                "Shipped",
                "Delivered",
                "Cancelled"
            ).forEach { filter ->

                val isSelected =
                    selectedFilter == filter

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

                    color =
                        if (isSelected) {
                            Color.White
                        } else {
                            TilloraTextSecondary
                        },

                    fontWeight =
                        if (isSelected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        },

                    style =
                        MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // =================================================
        // CONTENT
        // =================================================

        when {

            // =================================================
            // LOADING
            // =================================================

            isLoading -> {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.Center
                ) {

                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(24.dp)
                    )
                }
            }

            // =================================================
            // ERROR
            // =================================================

            error != null -> {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Text(
                        text =
                            error
                                ?: "Failed to load orders.",

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )
                }
            }

            // =================================================
            // EMPTY
            // =================================================

            filteredOrders.isEmpty() -> {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Text(
                        text =
                            if (selectedFilter == "All") {
                                "No orders found."
                            } else {
                                "No $selectedFilter orders found."
                            },

                        color =
                            TilloraTextSecondary,

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )
                }
            }

            // =================================================
            // ORDERS
            // =================================================

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),

                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,

                        // Space above bottom navigation
                        bottom = 100.dp
                    ),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
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
            }
        }
    }
}