package com.example.Tillora.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Tillora.components.ProductCard
import com.example.Tillora.components.CategoryChip
import com.example.Tillora.models.Product
import com.example.Tillora.viewmodels.ProductViewModel
import kotlinx.coroutines.delay

/*
|--------------------------------------------------------------------------
| PALETTE
|--------------------------------------------------------------------------
| Navy stays the single primary brand color. Everything else is a tint,
| shade, or a low-saturation neutral so navy keeps visual priority
| instead of competing with a second "loud" color.
*/

private val TilloraNavy = Color(0xFF0B1F3A)       // primary
private val TilloraNavyDeep = Color(0xFF071427)   // hero gradient end / pressed states
private val TilloraNavyLight = Color(0xFF1B3F6B)  // hero gradient mid / hover
private val TilloraNavySoft = Color(0xFFE7ECF3)   // tinted surfaces (selected chip bg, etc.)
private val TilloraAccent = Color(0xFFE0A93A)     // small warm accent — badges, "featured" tag only
private val TilloraBackground = Color(0xFFF5F7FA)
private val TilloraSurface = Color.White
private val TilloraText = Color(0xFF0F172A)
private val TilloraMuted = Color(0xFF64748B)
private val TilloraBorder = Color(0xFFE1E6ED)
private val TilloraError = Color(0xFFDC2626)

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

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    /*
    |--------------------------------------------------------------------------
    | FILTERING
    |--------------------------------------------------------------------------
    */

    val filteredProducts = products.filter { product ->
        val matchesCategory =
            selectedCategoryId == null || product.categoryId == selectedCategoryId

        val matchesSearch =
            searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    val featuredProducts = filteredProducts.take(6)
    val newArrivals = filteredProducts.takeLast(6)

    /*
    |--------------------------------------------------------------------------
    | MAIN
    |--------------------------------------------------------------------------
    */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TilloraBackground)
    ) {

        TilloraHeader(
            onBackClick = onBackClick,
            onCartClick = onCartClick,
            cartItemCount = cartItemCount
        )

        TilloraSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {

            isLoading -> {
                LoadingState(modifier = Modifier.weight(1f))
                return@Column
            }

            error != null -> {
                ErrorState(
                    message = error ?: "Unable to load products",
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier.weight(1f)
                )
                return@Column
            }
        }

        /*
        |--------------------------------------------------------------------------
        | STORE CONTENT
        |--------------------------------------------------------------------------
        */

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),

            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 120.dp
            ),

            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item(span = { GridItemSpan(maxLineSpan) }) {
                HeroBanner(
                    onShopClick = {
                        selectedCategoryId = null
                        searchQuery = ""
                    }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                CategorySection(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    itemCount = filteredProducts.size,
                    onSelect = { id ->
                        selectedCategoryId =
                            if (selectedCategoryId == id) null else id
                    }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(
                    title = "Featured Products",
                    subtitle = "Popular picks for you"
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                MovingProductRail(
                    products = featuredProducts,
                    prefix = "featured",
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(
                    title = "New Arrivals",
                    subtitle = "Fresh products just added"
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                MovingProductRail(
                    products = newArrivals,
                    prefix = "new",
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(
                    title = "Explore Everything",
                    subtitle = if (selectedCategoryId == null)
                        "Browse our complete collection"
                    else
                        "Products in this category",
                    titleSize = 21.sp
                )
            }

            items(
                items = filteredProducts,
                key = { "all_${it.id}" }
            ) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = { onAddToCart(product) },
                    onProductClick = { onProductClick(product) }
                )
            }

            if (filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState()
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun TilloraHeader(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TilloraSurface)
            .padding(start = 4.dp, end = 12.dp, top = 30.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TilloraNavy
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Tillora",
                color = TilloraNavy,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Wholesale supplies, delivered.",
                color = TilloraMuted,
                fontSize = 12.sp
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(TilloraNavySoft)
            ) {
                IconButton(onClick = onCartClick) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        modifier = Modifier.size(22.dp),
                        tint = TilloraNavy
                    )
                }
            }

            if (cartItemCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, end = 2.dp)
                        .size(18.dp)
                        .background(color = TilloraAccent, shape = CircleShape)
                        .border(width = 1.5.dp, color = TilloraSurface, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cartItemCount > 9) "9+" else cartItemCount.toString(),
                        color = TilloraNavyDeep,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| SEARCH BAR
|--------------------------------------------------------------------------
*/

@Composable
private fun TilloraSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false),

        placeholder = {
            Text(
                text = "Search products, categories and more",
                color = TilloraMuted,
                fontSize = 14.sp
            )
        },

        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TilloraNavy
            )
        },

        singleLine = true,
        shape = RoundedCornerShape(16.dp),

        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TilloraNavy,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = TilloraSurface,
            unfocusedContainerColor = TilloraSurface
        )
    )
}

/*
|--------------------------------------------------------------------------
| LOADING / ERROR / EMPTY STATES
|--------------------------------------------------------------------------
*/

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = TilloraNavy)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Loading your store...", color = TilloraMuted, fontSize = 13.sp)
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = TilloraError,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TilloraNavy)
        ) {
            Text("Try Again")
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(TilloraNavySoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TilloraNavy,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Nothing found",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TilloraText
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Try another search or category.",
            color = TilloraMuted,
            fontSize = 13.sp
        )
    }
}

/*
|--------------------------------------------------------------------------
| HERO BANNER
|--------------------------------------------------------------------------
*/

@Composable
private fun HeroBanner(onShopClick: () -> Unit) {

    val transition = rememberInfiniteTransition(label = "hero")

    val animatedOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroOffset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), clip = false),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(196.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(TilloraNavy, TilloraNavyLight, TilloraNavyDeep)
                    )
                )
        ) {

            // Decorative circles — quiet, low-contrast, never competing with the text
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer { translationX = animatedOffset }
                    .background(
                        color = Color.White.copy(alpha = 0.06f),
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = CircleShape
                    )
                    .align(Alignment.BottomEnd)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
            ) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(TilloraAccent.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "TILLORA WHOLESALE",
                        color = TilloraAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Bulk supplies for",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )

                Text(
                    text = "your business.",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onShopClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = TilloraNavy
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(text = "Shop Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| CATEGORY SECTION
|--------------------------------------------------------------------------
*/

@Composable
private fun CategorySection(
    categories: List<com.example.Tillora.models.Category>,
    selectedCategoryId: Int?,
    itemCount: Int,
    onSelect: (Int?) -> Unit
) {
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Shop by category",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TilloraText
                )
                Text(
                    text = "Find what you need faster",
                    fontSize = 12.sp,
                    color = TilloraMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(TilloraNavySoft)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "$itemCount items",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TilloraNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {

            item {
                CategoryPill(
                    text = "All",
                    selected = selectedCategoryId == null,
                    onClick = { onSelect(null) }
                )
            }

            items(items = categories, key = { it.id }) { category ->
                CategoryPill(
                    text = category.name,
                    selected = selectedCategoryId == category.id,
                    onClick = { onSelect(category.id) }
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| SECTION HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    titleSize: androidx.compose.ui.unit.TextUnit = 20.sp
) {
    Column(modifier = Modifier.padding(top = 6.dp)) {
        Text(
            text = title,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            color = TilloraText
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = subtitle, fontSize = 12.sp, color = TilloraMuted)
    }
}

/*
|--------------------------------------------------------------------------
| CATEGORY PILL
|--------------------------------------------------------------------------
*/

@Composable
private fun CategoryPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) TilloraNavy else TilloraSurface)
            .border(
                width = 1.dp,
                color = if (selected) TilloraNavy else TilloraBorder,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else TilloraText,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/*
|--------------------------------------------------------------------------
| MOVING PRODUCT RAIL
|--------------------------------------------------------------------------
*/

@Composable
private fun MovingProductRail(
    products: List<Product>,
    prefix: String,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(products) {
        if (products.size > 1) {
            while (true) {
                delay(3200)

                val next = (listState.firstVisibleItemIndex + 1)
                    .coerceAtMost(products.lastIndex)

                listState.animateScrollToItem(index = next)

                if (next == products.lastIndex) {
                    delay(1500)
                    listState.animateScrollToItem(index = 0)
                }
            }
        }
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 2.dp, end = 20.dp)
    ) {
        items(items = products, key = { "${prefix}_${it.id}" }) { product ->

            val scale by animateFloatAsState(
                targetValue = if (listState.firstVisibleItemIndex == products.indexOf(product))
                    1.01f
                else
                    1f,
                animationSpec = tween(400),
                label = "productScale"
            )

            Box(
                modifier = Modifier
                    .width(185.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                ProductCard(
                    product = product,
                    onAddToCart = { onAddToCart(product) },
                    onProductClick = { onProductClick(product) }
                )
            }
        }
    }
}