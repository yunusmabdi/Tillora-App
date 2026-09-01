package com.example.Tillora.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.Order
import com.example.Tillora.models.Product
import com.example.Tillora.screens.AccountScreen
import com.example.Tillora.screens.CartScreen
import com.example.Tillora.screens.CheckoutScreen
import com.example.Tillora.screens.EditProfileScreen
import com.example.Tillora.screens.HomeScreen
import com.example.Tillora.screens.OrderTrackingScreen
import com.example.Tillora.screens.OrdersScreen
import com.example.Tillora.screens.viewmodel.OrderViewModel
import com.example.Tillora.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
private enum class AppScreen {
    HOME,
    CART,
    CHECKOUT,
    ORDERS,
    ACCOUNT,
    EDIT_PROFILE,
    ORDER_TRACKING
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    cartItemCount: Int,
    onAddToCart: (Product) -> Unit,
    cartItems: List<CartItem>,
    onRemoveItem: (CartItem) -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onProductClick: (Product) -> Unit,
    onLogout: () -> Unit,
    onClearCart: () -> Unit
) {

    // =========================================================
    // NAVIGATION STATE
    // =========================================================

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    var selectedOrder by remember {
        mutableStateOf<Order?>(null)
    }

    var loadingOrder by remember {
        mutableStateOf(false)
    }

    var orderLoadError by remember {
        mutableStateOf<String?>(null)
    }

    val customer by authViewModel.customer.collectAsState()

    val orderViewModel: OrderViewModel = remember {
        OrderViewModel()
    }

    // =========================================================
    // ORDER TRACKING
    // =========================================================

    if (currentScreen == AppScreen.ORDER_TRACKING) {

        when {

            loadingOrder -> {

                androidx.compose.foundation.layout.Box(
                    modifier =
                        androidx.compose.ui.Modifier
                            .fillMaxSize(),

                    contentAlignment =
                        androidx.compose.ui.Alignment.Center
                ) {

                    androidx.compose.material3.CircularProgressIndicator()
                }
            }

            selectedOrder != null -> {

                OrderTrackingScreen(
                    order = selectedOrder!!,

                    onBackClick = {

                        selectedOrder = null
                        orderLoadError = null

                        currentScreen =
                            AppScreen.ORDERS

                        // Refresh the orders list so that
                        // the latest fulfillment/payment status
                        // is displayed.
                        orderViewModel.refreshOrders()
                    }
                )
            }

            orderLoadError != null -> {

                androidx.compose.foundation.layout.Column(
                    modifier =
                        androidx.compose.ui.Modifier
                            .fillMaxSize()
                            .padding(24.dp),

                    horizontalAlignment =
                        androidx.compose.ui.Alignment.CenterHorizontally,

                    verticalArrangement =
                        androidx.compose.foundation.layout.Arrangement.Center
                ) {

                    Text(
                        text =
                            orderLoadError
                                ?: "Unable to load order."
                    )

                    androidx.compose.material3.Button(
                        onClick = {

                            orderLoadError = null
                            currentScreen =
                                AppScreen.ORDERS
                        }
                    ) {

                        Text("Back to Orders")
                    }
                }
            }
        }

        return
    }

    // =========================================================
    // EDIT PROFILE
    // =========================================================

    if (currentScreen == AppScreen.EDIT_PROFILE) {

        if (customer != null) {

            EditProfileScreen(
                customer = customer!!,
                viewModel = authViewModel,

                onCancelClick = {

                    currentScreen =
                        AppScreen.ACCOUNT
                }
            )

        } else {

            currentScreen =
                AppScreen.ACCOUNT
        }

        return
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    if (currentScreen == AppScreen.CHECKOUT) {

        CheckoutScreen(
            cartItems = cartItems,

            onBackClick = {

                currentScreen =
                    AppScreen.CART
            },

            onOrderComplete = {

                // Payment completed.
                // Go directly to My Orders.
                currentScreen =
                    AppScreen.ORDERS

                orderViewModel.refreshOrders()
            },

            onClearCart = {

                onClearCart()
            }
        )

        return
    }

    // =========================================================
    // MAIN APP
    // =========================================================

    Scaffold(

        bottomBar = {

            NavigationBar {

                // =================================================
                // HOME
                // =================================================

                NavigationBarItem(
                    selected =
                        currentScreen == AppScreen.HOME,

                    onClick = {

                        selectedOrder = null

                        currentScreen =
                            AppScreen.HOME
                    },

                    icon = {

                        Icon(
                            imageVector =
                                Icons.Default.Home,

                            contentDescription =
                                "Home"
                        )
                    },

                    label = {

                        Text("Home")
                    }
                )

                // =================================================
                // CART
                // =================================================

                NavigationBarItem(
                    selected =
                        currentScreen == AppScreen.CART,

                    onClick = {

                        selectedOrder = null

                        currentScreen =
                            AppScreen.CART
                    },

                    icon = {

                        Icon(
                            imageVector =
                                Icons.Default.ShoppingCart,

                            contentDescription =
                                "Cart"
                        )
                    },

                    label = {

                        Text("Cart")
                    }
                )

                // =================================================
                // ORDERS
                // =================================================

                NavigationBarItem(
                    selected =
                        currentScreen == AppScreen.ORDERS,

                    onClick = {

                        selectedOrder = null

                        currentScreen =
                            AppScreen.ORDERS

                        orderViewModel.refreshOrders()
                    },

                    icon = {

                        Icon(
                            imageVector =
                                Icons.Default.ReceiptLong,

                            contentDescription =
                                "Orders"
                        )
                    },

                    label = {

                        Text("Orders")
                    }
                )

                // =================================================
                // ACCOUNT
                // =================================================

                NavigationBarItem(
                    selected =
                        currentScreen == AppScreen.ACCOUNT,

                    onClick = {

                        selectedOrder = null

                        currentScreen =
                            AppScreen.ACCOUNT
                    },

                    icon = {

                        Icon(
                            imageVector =
                                Icons.Default.AccountCircle,

                            contentDescription =
                                "Account"
                        )
                    },

                    label = {

                        Text("Account")
                    }
                )
            }
        }

    ) { paddingValues ->

        when (currentScreen) {

            // =====================================================
            // HOME
            // =====================================================

            AppScreen.HOME -> {

                HomeScreen(

                    onBackClick = {},

                    onCartClick = {

                        currentScreen =
                            AppScreen.CART
                    },

                    cartItemCount =
                        cartItemCount,

                    onProductClick =
                        onProductClick,

                    onAddToCart =
                        onAddToCart
                )
            }

            // =====================================================
            // CART
            // =====================================================

            AppScreen.CART -> {

                CartScreen(

                    cartItems =
                        cartItems,

                    onBackClick = {

                        currentScreen =
                            AppScreen.HOME
                    },

                    onRemoveItem =
                        onRemoveItem,

                    onIncreaseQuantity =
                        onIncreaseQuantity,

                    onDecreaseQuantity =
                        onDecreaseQuantity,

                    onCheckoutClick = {

                        if (
                            cartItems.isNotEmpty()
                        ) {

                            currentScreen =
                                AppScreen.CHECKOUT
                        }
                    },

                    contentPadding =
                        paddingValues
                )
            }

            // =====================================================
            // ORDERS
            // =====================================================

            AppScreen.ORDERS -> {

                OrdersScreen(

                    viewModel =
                        orderViewModel,

                    onOrderClick = { order ->

                        // Clear previous state.
                        selectedOrder = null
                        orderLoadError = null
                        loadingOrder = true

                        currentScreen =
                            AppScreen.ORDER_TRACKING

                        // =================================================
                        // IMPORTANT
                        // =================================================
                        //
                        // Do NOT use the order object directly.
                        //
                        // Fetch it again from Laravel so the tracking
                        // screen receives the latest fulfillment_status.
                        //
                        orderViewModel.getOrder(
                            id = order.id
                        ) { latestOrder ->

                            loadingOrder = false

                            if (
                                latestOrder != null
                            ) {

                                selectedOrder =
                                    latestOrder

                            } else {

                                orderLoadError =
                                    "Unable to load the latest order information."
                            }
                        }
                    }
                )
            }

            // =====================================================
            // ACCOUNT
            // =====================================================

            AppScreen.ACCOUNT -> {

                AccountScreen(

                    authViewModel =
                        authViewModel,

                    onEditProfileClick = {

                        currentScreen =
                            AppScreen.EDIT_PROFILE
                    },

                    onOrdersClick = {

                        currentScreen =
                            AppScreen.ORDERS

                        orderViewModel.refreshOrders()
                    },

                    onHelpClick = {
                        // TODO
                    },

                    onAboutClick = {
                        // TODO
                    },

                    onLogoutClick = {

                        onLogout()
                    }
                )
            }

            // =====================================================
            // CHECKOUT
            // =====================================================

            AppScreen.CHECKOUT -> {
                // Handled above Scaffold.
            }

            // =====================================================
            // EDIT PROFILE
            // =====================================================

            AppScreen.EDIT_PROFILE -> {
                // Handled above Scaffold.
            }

            // =====================================================
            // ORDER TRACKING
            // =====================================================

            AppScreen.ORDER_TRACKING -> {
                // Handled above Scaffold.
            }
        }
    }
}