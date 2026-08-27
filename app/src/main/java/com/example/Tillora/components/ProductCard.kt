package com.example.Tillora.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.Tillora.models.Product

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit,
    onProductClick: (Product) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable {
                onProductClick(product)
            }
            .padding(12.dp)
    ) {

        // Product image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {

            val imageUrl = product.image?.let {
                "http://192.168.1.32:8000/storage/$it"
            }

            if (imageUrl != null) {

                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    onSuccess = {
                        android.util.Log.d(
                            "PRODUCT_IMAGE",
                            "SUCCESS: $imageUrl"
                        )
                    },
                    onError = {
                        android.util.Log.e(
                            "PRODUCT_IMAGE",
                            "ERROR: $imageUrl",
                            it.result.throwable
                        )
                    }
                )

            } else {

                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = product.name,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF94A3B8)
                )
            }

            // Out of stock label
            if (product.isOutOfStock) {

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDC2626))
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                ) {
                    Text(
                        text = "Out of stock",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Product name
        Text(
            text = product.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        // Unit
        Text(
            text = product.unit,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Price + add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                if (product.hasDiscount) {
                    Text(
                        text = "KSh %.2f".format(product.sellingPrice),
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = "KSh %.2f".format(product.discountedPrice),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            IconButton(
                onClick = {
                    if (!product.isOutOfStock) {
                        onAddToCart(product)
                    }
                },
                enabled = !product.isOutOfStock
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add ${product.name} to cart",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}