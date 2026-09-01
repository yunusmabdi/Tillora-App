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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Tillora.components.CategoryChip
import com.example.Tillora.components.ProductCard
import com.example.Tillora.models.Product
import com.example.Tillora.viewmodels.ProductViewModel

@Composable
fun HomeScreen(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit = {},
    cartItemCount: Int = 0,
    onProductClick: (Product) -> Unit = {},
    onAddToCart: (Product) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ProductViewModel = viewModel()
) {

    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedCategoryId by remember {
        mutableStateOf<Int?>(null)
    }

    // =====================================================
    // FILTER PRODUCTS
    // =====================================================

    val filteredProducts = products.filter { product ->

        val matchesCategory =
            selectedCategoryId == null ||
                    product.categoryId == selectedCategoryId

        val matchesSearch =
            searchQuery.isBlank() ||
                    product.name.contains(
                        searchQuery,
                        ignoreCase = true
                    )

        matchesCategory && matchesSearch
    }

    // =====================================================
    // FEATURED / NEW ARRIVALS
    // =====================================================

    val featuredProducts =
        filteredProducts.take(6)

    val newArrivals =
        filteredProducts.takeLast(6)

    // =====================================================
    // MAIN SCREEN
    // =====================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
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
                    top = 35.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Keep back button for now
            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F172A)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Tillora",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "Shop your essentials",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            // =================================================
            // CART BUTTON
            // =================================================

            Box(
                contentAlignment = Alignment.TopEnd
            ) {

                IconButton(
                    onClick = onCartClick
                ) {

                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        modifier = Modifier.size(26.dp),
                        tint = Color(0xFF0F172A)
                    )
                }

                if (cartItemCount > 0) {

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = Color(0xFFDC2626),
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = cartItemCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =================================================
        // SEARCH
        // =================================================

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = {
                Text("Search products")
            },
            leadingIcon = {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // =================================================
        // LOADING
        // =================================================

        if (isLoading) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Loading products..."
                )
            }

            return@Column
        }

        // =================================================
        // ERROR
        // =================================================

        if (error != null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = error ?: "Unable to load products",
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {
                        viewModel.loadData()
                    }
                ) {

                    Text("Retry")
                }
            }

            return@Column
        }

        // =================================================
        // SCROLLABLE HOME CONTENT
        // =================================================

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),

            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,

                // Space above content
                top = 4.dp,

                // IMPORTANT:
                // Bottom navigation padding + extra spacing
                bottom = contentPadding.calculateBottomPadding() + 24.dp
            ),

            horizontalArrangement = Arrangement.spacedBy(12.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // =================================================
            // PROMOTIONAL BANNER
            // =================================================

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F172A)
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {

                        Text(
                            text = "SPECIAL OFFER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Up to 30% OFF",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Shop your favourites at great prices.",
                            fontSize = 14.sp,
                            color = Color(0xFFE2E8F0)
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Button(
                            onClick = {
                                selectedCategoryId = null
                                searchQuery = ""
                            }
                        ) {

                            Text("Shop Now")
                        }
                    }
                }
            }

            // =================================================
            // CATEGORIES
            // =================================================

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),

                        horizontalArrangement =
                            Arrangement.SpaceBetween,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Categories",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Text(
                            text = "${filteredProducts.size} products",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // Horizontal category scrolling
                    LazyRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        item {

                            CategoryChip(
                                name = "All",

                                selected =
                                    selectedCategoryId == null,

                                onClick = {
                                    selectedCategoryId = null
                                }
                            )
                        }

                        items(
                            items = categories,
                            key = {
                                it.id
                            }
                        ) { category ->

                            CategoryChip(
                                name = category.name,

                                selected =
                                    selectedCategoryId == category.id,

                                onClick = {

                                    selectedCategoryId =
                                        if (
                                            selectedCategoryId ==
                                            category.id
                                        ) {
                                            null
                                        } else {
                                            category.id
                                        }
                                }
                            )
                        }
                    }
                }
            }

            // =================================================
            // FEATURED TITLE
            // =================================================

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                Text(
                    text = "Featured Products",

                    modifier = Modifier.padding(
                        top = 8.dp
                    ),

                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            // =================================================
            // FEATURED PRODUCTS
            // =================================================

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),

                    contentPadding =
                        PaddingValues(horizontal = 4.dp)
                ) {

                    items(
                        items = featuredProducts,

                        key = {
                            "featured_${it.id}"
                        }
                    ) { product ->

                        Box(
                            modifier = Modifier.width(180.dp)
                        ) {

                            ProductCard(
                                product = product,

                                onAddToCart = {
                                    onAddToCart(product)
                                },

                                onProductClick = {
                                    onProductClick(product)
                                }
                            )
                        }
                    }
                }
            }

            // =================================================
            // NEW ARRIVALS TITLE
            // =================================================

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                Text(
                    text = "New Arrivals",

                    modifier = Modifier.padding(
                        top = 12.dp
                    ),

                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            // =================================================
            // NEW ARRIVALS
            // =================================================

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),

                    contentPadding =
                        PaddingValues(horizontal = 4.dp)
                ) {

                    items(
                        items = newArrivals,

                        key = {
                            "new_${it.id}"
                        }
                    ) { product ->

                        Box(
                            modifier = Modifier.width(180.dp)
                        ) {

                            ProductCard(
                                product = product,

                                onAddToCart = {
                                    onAddToCart(product)
                                },

                                onProductClick = {
                                    onProductClick(product)
                                }
                            )
                        }
                    }
                }
            }

            // =================================================
            // ALL PRODUCTS TITLE
            // =================================================

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                Column {

                    Text(
                        text = "All Products",

                        modifier = Modifier.padding(
                            top = 12.dp
                        ),

                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            if (selectedCategoryId == null) {
                                "Browse all products"
                            } else {
                                "Products in selected category"
                            },

                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // =================================================
            // ALL PRODUCTS GRID
            // =================================================

            items(
                items = filteredProducts,

                key = {
                    "all_${it.id}"
                }
            ) { product ->

                ProductCard(
                    product = product,

                    onAddToCart = {
                        onAddToCart(product)
                    },

                    onProductClick = {
                        onProductClick(product)
                    }
                )
            }

            // =================================================
            // NO PRODUCTS
            // =================================================

            if (filteredProducts.isEmpty()) {

                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "No products found",

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Try another category or search."
                        )
                    }
                }
            }
        }
    }
}