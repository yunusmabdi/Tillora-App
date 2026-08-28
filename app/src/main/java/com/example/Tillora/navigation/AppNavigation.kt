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
import com.example.Tillora.screens.EditProfileScreen
import com.example.Tillora.screens.HomeScreen
import com.example.Tillora.screens.OrderTrackingScreen
import com.example.Tillora.screens.OrdersScreen
import com.example.Tillora.viewmodel.AuthViewModel

private enum class AppScreen {
    HOME,
    CART,
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
    onCheckoutClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onLogout: () -> Unit
) {

    // =========================================================
    // CURRENT APP SCREEN
    // =========================================================

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    // =========================================================
    // SELECTED ORDER
    // =========================================================

    var selectedOrder by remember {
        mutableStateOf<Order?>(null)
    }

    // =========================================================
    // CUSTOMER
    // =========================================================

    val customer by authViewModel.customer.collectAsState()

    // =========================================================
    // EDIT PROFILE
    // =========================================================

    if (currentScreen == AppScreen.EDIT_PROFILE) {

        if (customer != null) {

            EditProfileScreen(
                customer = customer!!,
                viewModel = authViewModel,

                onCancelClick = {
                    currentScreen = AppScreen.ACCOUNT
                }
            )

        } else {

            currentScreen = AppScreen.ACCOUNT
        }

        return
    }

    // =========================================================
    // ORDER TRACKING
    // =========================================================

    if (currentScreen == AppScreen.ORDER_TRACKING) {

        if (selectedOrder != null) {

            OrderTrackingScreen(
                order = selectedOrder!!,

                onBackClick = {
                    selectedOrder = null
                    currentScreen = AppScreen.ORDERS
                }
            )

        } else {

            currentScreen = AppScreen.ORDERS
        }

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
                    selected = currentScreen == AppScreen.HOME,
                    onClick = {
                        currentScreen = AppScreen.HOME
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
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
                    selected = currentScreen == AppScreen.CART,
                    onClick = {
                        currentScreen = AppScreen.CART
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart"
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
                    selected = currentScreen == AppScreen.ORDERS,
                    onClick = {
                        currentScreen = AppScreen.ORDERS
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Orders"
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
                    selected = currentScreen == AppScreen.ACCOUNT,
                    onClick = {
                        currentScreen = AppScreen.ACCOUNT
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account"
                        )
                    },
                    label = {
                        Text("Account")
                    }
                )
            }
        }

    ) { paddingValues ->

        // =========================================================
        // SCREEN CONTENT
        // =========================================================

        when (currentScreen) {

            // =====================================================
            // HOME
            // =====================================================

            AppScreen.HOME -> {

                HomeScreen(
                    onBackClick = {},

                    onCartClick = {
                        currentScreen = AppScreen.CART
                    },

                    cartItemCount = cartItemCount,

                    onProductClick = onProductClick,

                    onAddToCart = onAddToCart
                )
            }

            // =====================================================
            // CART
            // =====================================================

            AppScreen.CART -> {

                CartScreen(
                    cartItems = cartItems,

                    onBackClick = {
                        currentScreen = AppScreen.HOME
                    },

                    onRemoveItem = onRemoveItem,

                    onIncreaseQuantity = onIncreaseQuantity,

                    onDecreaseQuantity = onDecreaseQuantity,

                    onCheckoutClick = onCheckoutClick,

                    contentPadding = paddingValues
                )
            }

            // =====================================================
            // ORDERS
            // =====================================================

            AppScreen.ORDERS -> {

                OrdersScreen(
                    onOrderClick = { order ->

                        selectedOrder = order

                        currentScreen = AppScreen.ORDER_TRACKING
                    }
                )
            }

            // =====================================================
            // ACCOUNT
            // =====================================================

            AppScreen.ACCOUNT -> {

                AccountScreen(
                    authViewModel = authViewModel,

                    onEditProfileClick = {
                        currentScreen = AppScreen.EDIT_PROFILE
                    },

                    onOrdersClick = {
                        currentScreen = AppScreen.ORDERS
                    },

                    onHelpClick = {
                        // TODO: Help
                    },

                    onAboutClick = {
                        // TODO: About
                    },

                    onLogoutClick = {
                        onLogout()
                    }
                )
            }

            // =====================================================
            // EDIT PROFILE
            // =====================================================

            AppScreen.EDIT_PROFILE -> {
                // Handled above before Scaffold.
            }

            // =====================================================
            // ORDER TRACKING
            // =====================================================

            AppScreen.ORDER_TRACKING -> {
                // Handled above before Scaffold.
            }
        }
    }
}