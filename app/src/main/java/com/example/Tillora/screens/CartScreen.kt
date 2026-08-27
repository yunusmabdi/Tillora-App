package com.example.Tillora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Tillora.components.CartItem

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onCheckoutClick: () -> Unit,
    contentPadding: PaddingValues
) {

    val subtotal = cartItems.sumOf {
        it.product.discountedPrice * it.quantity
    }

    val deliveryFee = if (cartItems.isEmpty()) {
        0.0
    } else {
        150.0
    }

    val total = subtotal + deliveryFee

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(contentPadding)
    ) {

        // =================================================
        // HEADER
        // =================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "My Cart",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        // =================================================
        // EMPTY CART
        // =================================================

        if (cartItems.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Your cart is empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Add products to your cart"
                )
            }

        } else {

            // =================================================
            // ENTIRE CART IS SCROLLABLE
            // =================================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),

                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 40.dp
                ),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // =================================================
                // CART ITEMS
                // =================================================

                items(
                    items = cartItems,
                    key = {
                        it.product.id
                    }
                ) { item ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // PRODUCT INFORMATION
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = item.product.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        text = "KSh %.2f each".format(
                                            item.product.discountedPrice
                                        ),
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp
                                    )
                                }

                                // REMOVE
                                IconButton(
                                    onClick = {
                                        onRemoveItem(item)
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove ${item.product.name}",
                                        tint = Color(0xFFDC2626)
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            // =================================================
                            // QUANTITY CONTROLS
                            // =================================================

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    IconButton(
                                        onClick = {
                                            onDecreaseQuantity(item)
                                        }
                                    ) {

                                        Text(
                                            text = "−",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = item.quantity.toString(),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = {
                                            onIncreaseQuantity(item)
                                        }
                                    ) {

                                        Text(
                                            text = "+",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "KSh %.2f".format(
                                        item.product.discountedPrice *
                                                item.quantity
                                    ),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // =================================================
                // ORDER SUMMARY
                // =================================================

                item {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {

                            Text(
                                text = "Order Summary",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            // SUBTOTAL
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text = "Subtotal",
                                    color = Color(0xFF64748B)
                                )

                                Text(
                                    text = "KSh %.2f".format(subtotal),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            // DELIVERY
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text = "Delivery",
                                    color = Color(0xFF64748B)
                                )

                                Text(
                                    text = "KSh %.2f".format(deliveryFee),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            // TOTAL
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text = "Total",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "KSh %.2f".format(total),
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(20.dp)
                            )

                            // CHECKOUT
                            Button(
                                onClick = onCheckoutClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    text = "Proceed to Checkout",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}