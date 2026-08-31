package com.example.Tillora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Tillora.api.ApiClient
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.Product
import com.example.Tillora.navigation.AppNavigation
import com.example.Tillora.screens.CheckoutScreen
import com.example.Tillora.screens.LoginScreen
import com.example.Tillora.screens.OtpVerificationScreen
import com.example.Tillora.screens.ProductDetailsScreen
import com.example.Tillora.screens.RegisterScreen
import com.example.Tillora.screens.WelcomeScreen
import com.example.Tillora.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ApiClient.initialize(applicationContext)

        setContent {
            TilloraApp()
        }
    }
}

@Composable
fun TilloraApp() {

    // =================================================
    // AUTHENTICATION
    // =================================================

    val authViewModel: AuthViewModel = viewModel()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.initialize(context)
    }

    // =================================================
    // CURRENT SCREEN
    // =================================================

    var currentScreen by remember {
        mutableStateOf("welcome")
    }

    // =================================================
    // SELECTED PRODUCT
    // =================================================

    var selectedProduct by remember {
        mutableStateOf<Product?>(null)
    }

    // =================================================
    // SHARED CART
    // =================================================

    var cartItems by remember {
        mutableStateOf<List<CartItem>>(emptyList())
    }

    // =================================================
    // ADD TO CART
    // =================================================

    fun addToCart(product: Product) {

        val existingItem = cartItems.find {
            it.product.id == product.id
        }

        cartItems =
            if (existingItem != null) {

                cartItems.map {

                    if (it.product.id == product.id) {

                        it.copy(
                            quantity = it.quantity + 1
                        )

                    } else {

                        it
                    }
                }

            } else {

                cartItems + CartItem(
                    product = product,
                    quantity = 1
                )
            }
    }

    // =================================================
    // NAVIGATION
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

                onBackClick = {
                    currentScreen = "welcome"
                },

                onRegisterClick = {
                    currentScreen = "register"
                },

                onLoginSuccess = {
                    currentScreen = "main"
                },

                viewModel = authViewModel
            )
        }

        // =================================================
        // REGISTER
        // =================================================

        "register" -> {

            RegisterScreen(

                onBackClick = {
                    currentScreen = "login"
                },

                onLoginClick = {
                    currentScreen = "login"
                },

                onOtpSent = {
                    currentScreen = "otp_verification"
                },

                viewModel = authViewModel
            )
        }

        // =================================================
        // OTP VERIFICATION
        // =================================================

        "otp_verification" -> {

            OtpVerificationScreen(

                onBackClick = {
                    currentScreen = "register"
                },

                onVerificationSuccess = {
                    currentScreen = "main"
                },

                viewModel = authViewModel
            )
        }

        // =================================================
        // MAIN APP
        // =================================================

        "main" -> {

            AppNavigation(

                authViewModel = authViewModel,

                cartItemCount = cartItems.sumOf {
                    it.quantity
                },

                cartItems = cartItems,

                onAddToCart = { product ->
                    addToCart(product)
                },

                onRemoveItem = { item ->

                    cartItems = cartItems.filter {
                        it.product.id != item.product.id
                    }
                },

                onIncreaseQuantity = { item ->

                    cartItems = cartItems.map {

                        if (it.product.id == item.product.id) {

                            it.copy(
                                quantity = it.quantity + 1
                            )

                        } else {

                            it
                        }
                    }
                },

                onDecreaseQuantity = { item ->

                    cartItems = cartItems.mapNotNull {

                        if (it.product.id == item.product.id) {

                            if (it.quantity > 1) {

                                it.copy(
                                    quantity = it.quantity - 1
                                )

                            } else {

                                null
                            }

                        } else {

                            it
                        }
                    }
                },

                onCheckoutClick = {
                    currentScreen = "checkout"
                },

                onProductClick = { product ->

                    selectedProduct = product

                    currentScreen = "product_details"
                },

                onCartClick = {
                    // Handled by AppNavigation.
                },

                onLogout = {

                    authViewModel.logout {

                        cartItems = emptyList()

                        selectedProduct = null

                        currentScreen = "login"
                    }
                }
            )
        }

        // =================================================
        // PRODUCT DETAILS
        // =================================================

        "product_details" -> {

            selectedProduct?.let { product ->

                ProductDetailsScreen(

                    product = product,

                    onBackClick = {

                        selectedProduct = null

                        currentScreen = "main"
                    },

                    onAddToCart = { productToAdd ->

                        addToCart(productToAdd)

                        currentScreen = "main"
                    }
                )
            }
        }

        // =================================================
        // CHECKOUT
        // =================================================

        "checkout" -> {

            CheckoutScreen(

                onBackClick = {
                    currentScreen = "main"
                }
            )
        }
    }
}