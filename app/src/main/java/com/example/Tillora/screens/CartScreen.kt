package com.example.Tillora.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.Tillora.components.CartItem

// ====================================================================
// TILLORA COLORS
// ====================================================================

private val TilloraNavy = Color(0xFF0B1F3A)
private val TilloraBackground = Color(0xFFF6F8FB)
private val TilloraSurface = Color.White
private val TilloraText = Color(0xFF0F172A)
private val TilloraMuted = Color(0xFF64748B)
private val TilloraBorder = Color(0xFFE2E8F0)
private val TilloraSoftNavy = Color(0xFFE8EEF5)


// ====================================================================
// CART SCREEN
// ====================================================================

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

    // ------------------------------------------------------------
    // CART SUBTOTAL
    // ------------------------------------------------------------

    val subtotal = cartItems.sumOf {
        it.product.discountedPrice * it.quantity
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TilloraBackground)
            .padding(contentPadding)
    ) {

        // ========================================================
        // HEADER
        // ========================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 10.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TilloraText
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "My Cart",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = TilloraText
                )

                if (cartItems.isNotEmpty()) {

                    val itemCount = cartItems.sumOf {
                        it.quantity
                    }

                    Text(
                        text = "$itemCount ${
                            if (itemCount == 1) "item" else "items"
                        }",
                        fontSize = 13.sp,
                        color = TilloraMuted
                    )
                }
            }

            if (cartItems.isNotEmpty()) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TilloraSoftNavy),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        modifier = Modifier.size(21.dp),
                        tint = TilloraNavy
                    )
                }
            }
        }

        // ========================================================
        // EMPTY CART
        // ========================================================

        if (cartItems.isEmpty()) {

            EmptyCartState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )

            return@Column
        }

        // ========================================================
        // CART LIST
        // ========================================================

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,

                // Extra space for bottom navigation
                bottom = contentPadding.calculateBottomPadding() + 120.dp
            ),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ====================================================
            // PRODUCTS
            // ====================================================

            items(
                items = cartItems,
                key = {
                    it.product.id
                }
            ) { item ->

                CartProductItem(
                    item = item,
                    onRemove = {
                        onRemoveItem(item)
                    },
                    onIncrease = {
                        onIncreaseQuantity(item)
                    },
                    onDecrease = {
                        onDecreaseQuantity(item)
                    }
                )
            }

            // ====================================================
            // ORDER SUMMARY
            // ====================================================

            item {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                OrderSummaryCard(
                    subtotal = subtotal,
                    onCheckoutClick = onCheckoutClick
                )
            }
        }
    }
}


// ====================================================================
// CART PRODUCT ITEM
// ====================================================================

@Composable
private fun CartProductItem(
    item: CartItem,
    onRemove: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    val product = item.product

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TilloraSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // ====================================================
            // TOP SECTION
            // ====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                // =================================================
                // PRODUCT IMAGE
                // =================================================

                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(
                            width = 1.dp,
                            color = TilloraBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    /*
                     * ProductCard.kt uses this exact URL format.
                     *
                     * Example:
                     *
                     * products/pishori-rice-25kg.jpg
                     *
                     * becomes:
                     *
                     * http://192.168.1.32:8000/storage/products/pishori-rice-25kg.jpg
                     */

                    val imageUrl = product.image?.let {
                        "http://192.168.1.32:8000/storage/$it"
                    }

                    if (imageUrl != null) {

                        AsyncImage(
                            model = imageUrl,
                            contentDescription = product.name,

                            modifier = Modifier
                                .fillMaxSize()
                                .clip(
                                    RoundedCornerShape(12.dp)
                                ),

                            onSuccess = {

                                Log.d(
                                    "CART_IMAGE",
                                    "SUCCESS: $imageUrl"
                                )
                            },

                            onError = {

                                Log.e(
                                    "CART_IMAGE",
                                    "ERROR: $imageUrl",
                                    it.result.throwable
                                )
                            }
                        )

                    } else {

                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = product.name,
                            modifier = Modifier.size(34.dp),
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                // =================================================
                // PRODUCT DETAILS
                // =================================================

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TilloraText,
                        maxLines = 2
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    // Wholesale unit
                    Text(
                        text = product.unit,
                        fontSize = 13.sp,
                        color = TilloraMuted
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = "KSh %.2f".format(
                            product.discountedPrice
                        ),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TilloraNavy
                    )

                    Text(
                        text = "per ${product.unit}",
                        fontSize = 11.sp,
                        color = TilloraMuted
                    )
                }

                // =================================================
                // REMOVE ITEM
                // =================================================

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove ${product.name}",
                        tint = TilloraMuted,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ====================================================
            // QUANTITY + ITEM TOTAL
            // ====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // =================================================
                // QUANTITY CONTROL
                // =================================================

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(
                            width = 1.dp,
                            color = TilloraBorder,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(36.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease quantity",
                            tint = TilloraNavy,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Text(
                        text = item.quantity.toString(),

                        modifier = Modifier.width(34.dp),

                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TilloraText,

                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(36.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase quantity",
                            tint = TilloraNavy,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                // =================================================
                // ITEM TOTAL
                // =================================================

                Column(
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        text = "Item total",
                        fontSize = 11.sp,
                        color = TilloraMuted
                    )

                    Text(
                        text = "KSh %.2f".format(
                            product.discountedPrice *
                                    item.quantity
                        ),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TilloraText
                    )
                }
            }
        }
    }
}


// ====================================================================
// ORDER SUMMARY
// ====================================================================

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    onCheckoutClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = TilloraSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
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
                color = TilloraText
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ====================================================
            // SUBTOTAL
            // ====================================================

            SummaryRow(
                label = "Subtotal",
                value = "KSh %.2f".format(subtotal)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ====================================================
            // DELIVERY
            // ====================================================

            SummaryRow(
                label = "Delivery",
                value = "Calculated at checkout"
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // Divider

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TilloraBorder)
            )

            Spacer(
                modifier = Modifier.height(17.dp)
            )

            // ====================================================
            // CURRENT CART TOTAL
            // ====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Subtotal",
                        fontSize = 15.sp,
                        color = TilloraMuted
                    )

                    Text(
                        text = "Final total calculated at checkout",
                        fontSize = 11.sp,
                        color = TilloraMuted
                    )
                }

                Text(
                    text = "KSh %.2f".format(subtotal),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TilloraNavy
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ====================================================
            // CHECKOUT BUTTON
            // ====================================================

            Button(
                onClick = onCheckoutClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape = RoundedCornerShape(13.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = TilloraNavy
                )
            ) {

                Text(
                    text = "Proceed to Checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Select your delivery zone at checkout",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 11.sp,
                color = TilloraMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}


// ====================================================================
// SUMMARY ROW
// ====================================================================

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            fontSize = 14.sp,
            color = TilloraMuted
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TilloraText
        )
    }
}


// ====================================================================
// EMPTY CART
// ====================================================================

@Composable
private fun EmptyCartState(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(TilloraSoftNavy),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = TilloraNavy
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Your cart is empty",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = TilloraText
        )

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        Text(
            text = "Add wholesale products to start building your order.",
            fontSize = 14.sp,
            color = TilloraMuted,
            textAlign = TextAlign.Center
        )
    }
}