package com.example.Tillora.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Tillora.models.Order
import com.example.Tillora.models.OrderItem
import com.example.Tillora.ui.theme.TilloraBackground
import com.example.Tillora.ui.theme.TilloraBorder
import com.example.Tillora.ui.theme.TilloraNavy
import com.example.Tillora.ui.theme.TilloraTextPrimary
import com.example.Tillora.ui.theme.TilloraTextSecondary
import java.util.Locale

@Composable
fun OrderTrackingScreen(
    order: Order,
    onBackClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TilloraBackground)
    ) {

        // =====================================================
        // TOP BAR
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 32.dp,
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
                    tint = TilloraNavy
                )
            }

            Column(
                modifier = Modifier.padding(start = 4.dp)
            ) {

                Text(
                    text = "Order Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TilloraTextPrimary
                )

                Text(
                    text = "#${order.invoiceNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TilloraTextSecondary
                )
            }
        }

        // =====================================================
        // CONTENT
        // =====================================================

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 32.dp
            ),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // =================================================
            // ORDER HEADER
            // =================================================

            item {
                OrderHeader(order)
            }

            // =================================================
            // DELIVERY ADDRESS
            // =================================================

            if (!order.deliveryAddress.isNullOrBlank()) {

                item {
                    DeliveryCard(
                        address = order.deliveryAddress
                    )
                }
            }

            // =================================================
            // TRACKING
            // =================================================

            item {
                TrackingCard(
                    status = order.fulfillmentStatus
                )
            }

            // =================================================
            // ORDER ITEMS TITLE
            // =================================================

            item {

                Text(
                    text = "Order Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TilloraTextPrimary
                )
            }

            // =================================================
            // ORDER ITEMS
            // =================================================

            items(
                items = order.items,
                key = { it.id }
            ) { item ->

                OrderItemRow(
                    item = item
                )
            }

            // =================================================
            // ORDER SUMMARY
            // =================================================

            item {
                OrderSummary(order)
            }

            // =================================================
            // PAYMENT
            // =================================================

            item {
                PaymentCard(order)
            }

            // =================================================
            // NOTES
            // =================================================

            if (!order.notes.isNullOrBlank()) {

                item {
                    NotesCard(
                        notes = order.notes
                    )
                }
            }
        }
    }
}


// =============================================================
// ORDER HEADER
// =============================================================

@Composable
private fun OrderHeader(
    order: Order
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(TilloraNavy)
            .padding(20.dp)
    ) {

        Text(
            text = "Order #${order.invoiceNumber}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = order.saleDate,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.75f)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Total Amount",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.75f)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = formatCurrency(order.totalAmount),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


// =============================================================
// DELIVERY CARD
// =============================================================

@Composable
private fun DeliveryCard(
    address: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    TilloraNavy.copy(alpha = 0.10f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = TilloraNavy
            )
        }

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Delivery Address",
                style = MaterialTheme.typography.bodySmall,
                color = TilloraTextSecondary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TilloraTextPrimary
            )
        }
    }
}


// =============================================================
// TRACKING CARD
// =============================================================

@Composable
private fun TrackingCard(
    status: String
) {

    val normalizedStatus =
        status
            .trim()
            .lowercase(Locale.ROOT)

    /*
     * These MUST match Laravel's fulfillment_status values.
     *
     * pending
     * preparing
     * ready
     * out_for_delivery
     * delivered
     */

    val steps = listOf(
        "pending",
        "preparing",
        "ready",
        "out_for_delivery",
        "delivered"
    )

    val currentStep =
        steps.indexOf(normalizedStatus)
            .takeIf { it >= 0 }
            ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = "Order Tracking",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TilloraTextPrimary
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        steps.forEachIndexed { index, step ->

            TrackingStep(
                title = trackingLabel(step),

                isCompleted =
                    index <= currentStep,

                isCurrent =
                    index == currentStep,

                showLine =
                    index < steps.lastIndex
            )
        }
    }
}


// =============================================================
// TRACKING LABEL
// =============================================================

private fun trackingLabel(
    status: String
): String {

    return when (status) {

        "pending" ->
            "Placed"

        "preparing" ->
            "Processing"

        "ready" ->
            "Ready"

        "out_for_delivery" ->
            "Out for Delivery"

        "delivered" ->
            "Delivered"

        else ->
            status
                .replace("_", " ")
                .replaceFirstChar {
                    it.uppercase()
                }
    }
}


// =============================================================
// TRACKING STEP
// =============================================================

@Composable
private fun TrackingStep(
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    showLine: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) {
                            TilloraNavy
                        } else {
                            TilloraBorder
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (isCompleted) {

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (showLine) {

                Box(
                    modifier = Modifier
                        .size(
                            width = 2.dp,
                            height = 30.dp
                        )
                        .background(
                            if (isCompleted) {
                                TilloraNavy
                            } else {
                                TilloraBorder
                            }
                        )
                )
            }
        }

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column(
            modifier = Modifier.padding(top = 5.dp)
        ) {

            Text(
                text = title,

                style =
                    MaterialTheme.typography.bodyMedium,

                fontWeight =
                    if (isCurrent) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },

                color =
                    if (isCompleted) {
                        TilloraTextPrimary
                    } else {
                        TilloraTextSecondary
                    }
            )

            if (isCurrent) {

                Text(
                    text = "Current status",

                    style =
                        MaterialTheme.typography.bodySmall,

                    color =
                        TilloraNavy
                )
            }
        }
    }
}


// =============================================================
// ORDER ITEM
// =============================================================

@Composable
private fun OrderItemRow(
    item: OrderItem
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TilloraBackground),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = TilloraNavy,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text =
                    item.product?.name ?: "Product",

                style =
                    MaterialTheme.typography.bodyLarge,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    TilloraTextPrimary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "Qty: ${item.quantity}",

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    TilloraTextSecondary
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text =
                    formatCurrency(item.unitPrice),

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    TilloraTextSecondary
            )
        }

        Text(
            text =
                formatCurrency(item.lineTotal),

            style =
                MaterialTheme.typography.bodyLarge,

            fontWeight =
                FontWeight.Bold,

            color =
                TilloraNavy
        )
    }
}


// =============================================================
// ORDER SUMMARY
// =============================================================

@Composable
private fun OrderSummary(
    order: Order
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = "Order Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TilloraTextPrimary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        SummaryRow(
            label = "Subtotal",
            value = formatCurrency(order.subtotal)
        )

        if (order.discountAmount > 0) {

            SummaryRow(
                label = "Discount",
                value =
                    "- ${formatCurrency(order.discountAmount)}"
            )
        }

        SummaryRow(
            label = "Tax",
            value = formatCurrency(order.tax)
        )

        if (order.deliveryFee > 0) {

            SummaryRow(
                label = "Delivery Fee",
                value =
                    formatCurrency(order.deliveryFee)
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(TilloraBorder)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TilloraTextPrimary
            )

            Text(
                text = formatCurrency(order.totalAmount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TilloraNavy
            )
        }
    }
}


// =============================================================
// SUMMARY ROW
// =============================================================

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TilloraTextSecondary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TilloraTextPrimary
        )
    }
}


// =============================================================
// PAYMENT CARD
// =============================================================

@Composable
private fun PaymentCard(
    order: Order
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    TilloraNavy.copy(alpha = 0.10f)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                tint = TilloraNavy
            )
        }

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.bodySmall,
                color = TilloraTextSecondary
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text =
                    order.paymentMethod
                        ?: "Not specified",

                style =
                    MaterialTheme.typography.bodyLarge,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    TilloraTextPrimary
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Paid: ${formatCurrency(order.amountPaid)}",

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    TilloraTextSecondary
            )
        }
    }
}


// =============================================================
// NOTES CARD
// =============================================================

@Composable
private fun NotesCard(
    notes: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = "Order Notes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TilloraTextPrimary
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = notes,
            style = MaterialTheme.typography.bodyMedium,
            color = TilloraTextSecondary
        )
    }
}


// =============================================================
// CURRENCY
// =============================================================

private fun formatCurrency(
    amount: Double
): String {

    return "KSh ${
        String.format(
            Locale.US,
            "%,.2f",
            amount
        )
    }"
}