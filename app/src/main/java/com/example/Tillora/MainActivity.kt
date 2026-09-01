package com.example.Tillora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.Tillora.api.ApiClient
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.Product
import com.example.Tillora.navigation.AppNavigation
import com.example.Tillora.screens.LoginScreen
import com.example.Tillora.screens.OtpVerificationScreen
import com.example.Tillora.screens.RegisterScreen
import com.example.Tillora.screens.WelcomeScreen
import com.example.Tillora.viewmodel.AuthViewModel
import androidx.compose.runtime.LaunchedEffect
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // =====================================================
        // INITIALIZE API CLIENT
        // =====================================================
        //
        // AuthViewModel accesses ApiClient when it is created.
        // Therefore ApiClient MUST be initialized first.
        //
        ApiClient.initialize(applicationContext)

        setContent {

            MaterialTheme {

                // =================================================
                // AUTH VIEW MODEL
                // =================================================

                val authViewModel = remember {
                    AuthViewModel()
                }

                LaunchedEffect(Unit) {
                    authViewModel.initialize(applicationContext)
                }

                // =================================================
                // CURRENT SCREEN
                // =================================================

                var currentScreen by remember {
                    mutableStateOf("welcome")
                }

                // =================================================
                // CART
                // =================================================

                var cartItems by remember {
                    mutableStateOf<List<CartItem>>(emptyList())
                }

                // =================================================
                // ADD TO CART
                // =================================================

                fun addToCart(product: Product) {

                    val existingItem =
                        cartItems.find {
                            it.product.id == product.id
                        }

                    cartItems =
                        if (existingItem != null) {

                            cartItems.map {

                                if (
                                    it.product.id ==
                                    product.id
                                ) {

                                    it.copy(
                                        quantity =
                                            it.quantity + 1
                                    )

                                } else {

                                    it
                                }
                            }

                        } else {

                            cartItems +
                                    CartItem(
                                        product = product,
                                        quantity = 1
                                    )
                        }
                }

                // =================================================
                // APPLICATION NAVIGATION
                // =================================================

                when (currentScreen) {

                    // =================================================
                    // WELCOME
                    // =================================================

                    "welcome" -> {

                        WelcomeScreen(

                            onLoginClick = {

                                currentScreen = "login"
                            },

                            // KEEP DEMO LOGIN
                            onDemoClick = {

                                authViewModel.demoLogin {

                                    currentScreen = "main"
                                }
                            }
                        )
                    }

                    // =================================================
                    // LOGIN
                    // =================================================

                    "login" -> {

                        LoginScreen(

                            viewModel = authViewModel,

                            onLoginSuccess = {

                                currentScreen = "main"
                            },

                            onBackClick = {

                                currentScreen = "welcome"
                            }
                        )
                    }

                    // =================================================
                    // REGISTER
                    // =================================================

                    "register" -> {

                        RegisterScreen(

                            viewModel = authViewModel,

                            onBackClick = {

                                currentScreen = "welcome"
                            },

                            onLoginClick = {

                                currentScreen = "login"
                            },

                            onOtpSent = {

                                currentScreen = "otp"
                            }
                        )
                    }

                    // =================================================
                    // OTP VERIFICATION
                    // =================================================

                    "otp" -> {

                        OtpVerificationScreen(

                            viewModel = authViewModel,

                            onVerificationSuccess = {

                                currentScreen = "main"
                            },

                            onBackClick = {

                                currentScreen = "register"
                            }
                        )
                    }

                    // =================================================
                    // MAIN APPLICATION
                    // =================================================

                    "main" -> {

                        AppNavigation(

                            authViewModel = authViewModel,

                            // -----------------------------------------
                            // CART COUNT
                            // -----------------------------------------

                            cartItemCount =
                                cartItems.sumOf {
                                    it.quantity
                                },

                            // -----------------------------------------
                            // ADD TO CART
                            // -----------------------------------------

                            onAddToCart = { product ->

                                addToCart(product)
                            },

                            // -----------------------------------------
                            // CART ITEMS
                            // -----------------------------------------

                            cartItems = cartItems,

                            // -----------------------------------------
                            // REMOVE ITEM
                            // -----------------------------------------

                            onRemoveItem = { item ->

                                cartItems =
                                    cartItems.filterNot {

                                        it.product.id ==
                                                item.product.id
                                    }
                            },

                            // -----------------------------------------
                            // INCREASE QUANTITY
                            // -----------------------------------------

                            onIncreaseQuantity = { item ->

                                cartItems =
                                    cartItems.map {

                                        if (
                                            it.product.id ==
                                            item.product.id
                                        ) {

                                            it.copy(
                                                quantity =
                                                    it.quantity + 1
                                            )

                                        } else {

                                            it
                                        }
                                    }
                            },

                            // -----------------------------------------
                            // DECREASE QUANTITY
                            // -----------------------------------------

                            onDecreaseQuantity = { item ->

                                cartItems =
                                    cartItems.mapNotNull {

                                        if (
                                            it.product.id ==
                                            item.product.id
                                        ) {

                                            if (
                                                it.quantity > 1
                                            ) {

                                                it.copy(
                                                    quantity =
                                                        it.quantity - 1
                                                )

                                            } else {

                                                null
                                            }

                                        } else {

                                            it
                                        }
                                    }
                            },

                            // -----------------------------------------
                            // PRODUCT CLICK
                            // -----------------------------------------
                            //
                            // Product details navigation is handled
                            // inside AppNavigation.
                            //
                            onProductClick = { product ->

                                // Handled by AppNavigation.
                            },

                            // -----------------------------------------
                            // LOGOUT
                            // -----------------------------------------

                            onLogout = {

                                authViewModel.logout {

                                    cartItems =
                                        emptyList()

                                    currentScreen =
                                        "welcome"
                                }
                            },

                            // -----------------------------------------
                            // CLEAR CART
                            // -----------------------------------------

                            onClearCart = {

                                cartItems =
                                    emptyList()
                            }
                        )
                    }
                }
            }
        }
    }
}