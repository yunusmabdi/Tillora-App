package com.example.Tillora.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Tillora.models.Order
import com.example.Tillora.ui.theme.StatusCancelled
import com.example.Tillora.ui.theme.StatusDelivered
import com.example.Tillora.ui.theme.StatusProcessing
import com.example.Tillora.ui.theme.StatusShipped
import com.example.Tillora.ui.theme.TilloraBorder
import com.example.Tillora.ui.theme.TilloraNavy
import com.example.Tillora.ui.theme.TilloraTextPrimary
import com.example.Tillora.ui.theme.TilloraTextSecondary
import java.util.Locale

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {

    val statusColor = when (order.status.lowercase()) {
        "processing" -> StatusProcessing
        "shipped" -> StatusShipped
        "delivered" -> StatusDelivered
        "cancelled" -> StatusCancelled
        else -> TilloraNavy
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = TilloraBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {

        // =====================================================
        // ORDER HEADER
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "#${order.invoiceNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TilloraTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = order.saleDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = TilloraTextSecondary
                )
            }

            // =================================================
            // STATUS
            // =================================================

            Text(
                text = order.status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // =====================================================
        // ORDER SUMMARY
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = "${order.items.size} Items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TilloraTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Order Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = TilloraTextSecondary
                )
            }

            Text(
                text = "KSh ${String.format(Locale.US, "%,.2f", order.totalAmount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TilloraNavy
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // =====================================================
        // VIEW DETAILS
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "View Details",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = TilloraNavy
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View order details",
                tint = TilloraNavy
            )
        }
    }
}