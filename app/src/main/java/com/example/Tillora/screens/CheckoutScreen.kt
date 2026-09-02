package com.example.Tillora.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Note
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Tillora.api.ApiClient
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.CheckoutItemRequest
import com.example.Tillora.models.ConfirmPaymentRequest
import com.example.Tillora.models.CreateOrderRequest
import com.example.Tillora.models.DeliveryZone
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.util.Locale

// =====================================================
// TILLORA COLORS
// =====================================================

private val TilloraNavy = Color(0xFF0B1F3A)
private val TilloraNavyLight = Color(0xFF17365D)
private val TilloraBackground = Color(0xFFF6F8FB)
private val TilloraBorder = Color(0xFFE2E7EE)
private val TilloraText = Color(0xFF172033)
private val TilloraMuted = Color(0xFF687386)
private val TilloraGreen = Color(0xFF159447)
private val TilloraRed = Color(0xFFC62828)
private val White = Color.White


// =====================================================
// CHECKOUT SCREEN
// =====================================================

@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onOrderComplete: () -> Unit,
    onClearCart: () -> Unit
) {

    val scope = rememberCoroutineScope()

    // -------------------------------------------------
    // FORM STATE
    // -------------------------------------------------

    var deliveryAddress by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var deliveryZones by remember {
        mutableStateOf<List<DeliveryZone>>(emptyList())
    }

    var selectedZone by remember {
        mutableStateOf<DeliveryZone?>(null)
    }

    var zonesLoading by remember {
        mutableStateOf(true)
    }

    var zonesError by remember {
        mutableStateOf<String?>(null)
    }

    // -------------------------------------------------
    // PAYMENT OPTION
    // -------------------------------------------------

    var paymentOption by remember {
        mutableStateOf("full")
    }

    // -------------------------------------------------
    // ORDER STATE
    // -------------------------------------------------

    var orderLoading by remember {
        mutableStateOf(false)
    }

    var createdOrderId by remember {
        mutableStateOf<Int?>(null)
    }

    var requiredPayment by remember {
        mutableStateOf(0.0)
    }

    // -------------------------------------------------
    // PAYMENT STATE
    // -------------------------------------------------

    var paymentLoading by remember {
        mutableStateOf(false)
    }

    var paymentMethod by remember {
        mutableStateOf("mpesa")
    }

    var transactionReference by remember {
        mutableStateOf("")
    }

    // -------------------------------------------------
    // MESSAGES
    // -------------------------------------------------

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var successMessage by remember {
        mutableStateOf<String?>(null)
    }

    // -------------------------------------------------
    // LOCAL DISPLAY TOTALS
    // -------------------------------------------------

    val subtotal = cartItems.sumOf {
        it.totalPrice
    }

    val tax = subtotal * 0.16

    val deliveryFee =
        selectedZone?.fee ?: 0.0

    val total =
        subtotal + tax + deliveryFee

    // -------------------------------------------------
    // LOAD DELIVERY ZONES
    // -------------------------------------------------

    LaunchedEffect(Unit) {

        try {

            zonesLoading = true
            zonesError = null

            val response =
                ApiClient.api.getDeliveryZones()

            if (response.success) {

                deliveryZones =
                    response.delivery_zones

            } else {

                zonesError =
                    response.message
                        ?: "Unable to load delivery zones."
            }

        } catch (e: Exception) {

            Log.e(
                "CHECKOUT",
                "Failed to load delivery zones",
                e
            )

            zonesError =
                e.message
                    ?: "Unable to load delivery zones."

        } finally {

            zonesLoading = false
        }
    }

    // =================================================
    // UI
    // =================================================

    Scaffold(
        containerColor = TilloraBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // -----------------------------------------
            // HEADER
            // -----------------------------------------

            CheckoutHeader(
                onBack = onBackClick
            )

            // -----------------------------------------
            // CONTENT
            // -----------------------------------------

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 24.dp
                ),
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                // -------------------------------------
                // INTRO
                // -------------------------------------

                item {

                    Column {

                        Text(
                            text = "Almost there!",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = TilloraText
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Review your order and complete your delivery details.",
                            fontSize = 14.sp,
                            color = TilloraMuted
                        )
                    }
                }

                // -------------------------------------
                // ORDER
                // -------------------------------------

                item {

                    SectionTitle(
                        icon = Icons.Outlined.ShoppingBag,
                        title = "Your order"
                    )
                }

                item {

                    OrderItemsCard(
                        cartItems = cartItems
                    )
                }

                // -------------------------------------
                // DELIVERY
                // -------------------------------------

                item {

                    SectionTitle(
                        icon = Icons.Outlined.LocationOn,
                        title = "Delivery details"
                    )
                }

                item {

                    DeliveryDetailsCard(
                        deliveryAddress = deliveryAddress,
                        onAddressChange = {
                            deliveryAddress = it
                            errorMessage = null
                        },
                        deliveryZones = deliveryZones,
                        selectedZone = selectedZone,
                        onZoneSelected = {
                            selectedZone = it
                            errorMessage = null
                        },
                        zonesLoading = zonesLoading,
                        zonesError = zonesError,
                        notes = notes,
                        onNotesChange = {
                            notes = it
                        }
                    )
                }

                // -------------------------------------
                // PAYMENT OPTION
                // -------------------------------------

                item {

                    SectionTitle(
                        icon = Icons.Outlined.Money,
                        title = "Payment option"
                    )
                }

                item {

                    PaymentOptionCard(
                        selected =
                            paymentOption == "full",
                        title = "Pay in full",
                        subtitle =
                            "Pay the complete order amount now",
                        amount = total,
                        onClick = {
                            paymentOption = "full"
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    PaymentOptionCard(
                        selected =
                            paymentOption == "advance",
                        title = "Pay 50% advance",
                        subtitle =
                            "Pay half now and the balance later",
                        amount = total * 0.50,
                        onClick = {
                            paymentOption = "advance"
                        }
                    )
                }

                // -------------------------------------
                // SUMMARY
                // -------------------------------------

                item {

                    SectionTitle(
                        icon = Icons.Outlined.Note,
                        title = "Order summary"
                    )
                }

                item {

                    OrderSummaryCard(
                        subtotal = subtotal,
                        tax = tax,
                        deliveryFee = deliveryFee,
                        total = total
                    )
                }

                // -------------------------------------
                // ORDER CREATED
                // -------------------------------------

                if (createdOrderId != null) {

                    item {

                        PaymentRequiredBanner(
                            amount = requiredPayment
                        )
                    }

                    item {

                        SectionTitle(
                            icon = Icons.Outlined.Money,
                            title = "Complete payment"
                        )
                    }

                    item {

                        PaymentCard(
                            amount = requiredPayment,
                            paymentMethod = paymentMethod,
                            onPaymentMethodChange = {
                                paymentMethod = it
                            },
                            transactionReference =
                                transactionReference,
                            onReferenceChange = {
                                transactionReference = it
                                errorMessage = null
                            },
                            loading = paymentLoading,
                            onConfirm = {

                                if (
                                    transactionReference
                                        .isBlank()
                                ) {

                                    errorMessage =
                                        "Please enter your transaction reference."

                                    return@PaymentCard
                                }

                                val orderId =
                                    createdOrderId

                                if (orderId == null) {

                                    errorMessage =
                                        "Order ID is missing."

                                    return@PaymentCard
                                }

                                scope.launch {

                                    paymentLoading = true
                                    errorMessage = null

                                    try {

                                        val request =
                                            ConfirmPaymentRequest(
                                                amount_paid =
                                                    requiredPayment,

                                                payment_method =
                                                    paymentMethod,

                                                transaction_reference =
                                                    transactionReference
                                                        .trim()
                                            )

                                        Log.d(
                                            "CHECKOUT",
                                            "Confirming payment for order $orderId"
                                        )

                                        val response =
                                            ApiClient.api
                                                .confirmPayment(
                                                    id = orderId,
                                                    request = request
                                                )

                                        if (
                                            response.success
                                        ) {

                                            onClearCart()

                                            successMessage =
                                                response.message
                                                    ?: "Payment confirmed successfully."

                                            onOrderComplete()

                                        } else {

                                            errorMessage =
                                                response.message
                                                    ?: "Payment confirmation failed."
                                        }

                                    } catch (
                                        e: HttpException
                                    ) {

                                        val body =
                                            e.response()
                                                ?.errorBody()
                                                ?.string()

                                        Log.e(
                                            "CHECKOUT",
                                            "Payment HTTP ${e.code()}: $body"
                                        )

                                        errorMessage =
                                            parseApiError(
                                                body,
                                                e.code()
                                            )

                                    } catch (
                                        e: Exception
                                    ) {

                                        Log.e(
                                            "CHECKOUT",
                                            "Payment failed",
                                            e
                                        )

                                        errorMessage =
                                            e.message
                                                ?: "Payment confirmation failed."

                                    } finally {

                                        paymentLoading = false
                                    }
                                }
                            }
                        )
                    }
                }

                // -------------------------------------
                // ERROR
                // -------------------------------------

                if (
                    errorMessage != null
                ) {

                    item {

                        MessageCard(
                            message =
                                errorMessage!!,
                            isError = true
                        )
                    }
                }

                // -------------------------------------
                // SUCCESS
                // -------------------------------------

                if (
                    successMessage != null
                ) {

                    item {

                        MessageCard(
                            message =
                                successMessage!!,
                            isError = false
                        )
                    }
                }
            }

            // =================================================
            // PLACE ORDER BAR
            // =================================================

            if (
                createdOrderId == null
            ) {

                CheckoutBottomBar(
                    amount =
                        if (
                            paymentOption == "advance"
                        ) {
                            total * 0.50
                        } else {
                            total
                        },

                    loading = orderLoading,

                    enabled =
                        !zonesLoading &&
                                selectedZone != null &&
                                deliveryAddress.isNotBlank() &&
                                cartItems.isNotEmpty(),

                    paymentOption =
                        paymentOption,

                    onClick = {

                        // -----------------------------
                        // VALIDATION
                        // -----------------------------

                        if (
                            cartItems.isEmpty()
                        ) {

                            errorMessage =
                                "Your cart is empty."

                            return@CheckoutBottomBar
                        }

                        if (
                            deliveryAddress.isBlank()
                        ) {

                            errorMessage =
                                "Please enter your delivery address."

                            return@CheckoutBottomBar
                        }

                        val zone =
                            selectedZone

                        if (zone == null) {

                            errorMessage =
                                "Please select a delivery zone."

                            return@CheckoutBottomBar
                        }

                        // -----------------------------
                        // CREATE ORDER
                        // -----------------------------

                        scope.launch {

                            orderLoading = true
                            errorMessage = null
                            successMessage = null

                            try {

                                val request =
                                    CreateOrderRequest(

                                        items =
                                            cartItems.map { cartItem ->

                                                CheckoutItemRequest(
                                                    product_id =
                                                        cartItem
                                                            .product
                                                            .id,

                                                    quantity =
                                                        cartItem
                                                            .quantity
                                                )
                                            },

                                        delivery_address =
                                            deliveryAddress
                                                .trim(),

                                        delivery_zone_id =
                                            zone.id,

                                        payment_option =
                                            paymentOption,

                                        notes =
                                            notes
                                                .trim()
                                                .ifBlank {
                                                    null
                                                }
                                    )

                                // -------------------------
                                // DEBUG REQUEST
                                // -------------------------

                                Log.d(
                                    "CHECKOUT",
                                    "Creating order:"
                                )

                                Log.d(
                                    "CHECKOUT",
                                    "items=${request.items}"
                                )

                                Log.d(
                                    "CHECKOUT",
                                    "delivery_address=${request.delivery_address}"
                                )

                                Log.d(
                                    "CHECKOUT",
                                    "delivery_zone_id=${request.delivery_zone_id}"
                                )

                                Log.d(
                                    "CHECKOUT",
                                    "payment_option=${request.payment_option}"
                                )

                                Log.d(
                                    "CHECKOUT",
                                    "notes=${request.notes}"
                                )

                                // -------------------------
                                // API CALL
                                // -------------------------

                                val response =
                                    ApiClient.api
                                        .createOrder(
                                            request
                                        )

                                // -------------------------
                                // RESPONSE
                                // -------------------------

                                if (
                                    response.success &&
                                    response.order != null
                                ) {

                                    val order =
                                        response.order

                                    createdOrderId =
                                        order.id

                                    /*
                                     * Laravel is authoritative
                                     * for the actual order total.
                                     */

                                    requiredPayment =
                                        if (
                                            paymentOption ==
                                            "full"
                                        ) {

                                            order.totalAmount

                                        } else {

                                            order.totalAmount *
                                                    0.50
                                        }

                                    successMessage =
                                        response.message
                                            ?: "Order created successfully. Complete your payment."

                                } else {

                                    errorMessage =
                                        response.message
                                            ?: "Unable to create order."
                                }

                            } catch (
                                e: HttpException
                            ) {

                                val body =
                                    e.response()
                                        ?.errorBody()
                                        ?.string()

                                Log.e(
                                    "CHECKOUT",
                                    "Order HTTP ${e.code()}: $body"
                                )

                                errorMessage =
                                    parseApiError(
                                        body,
                                        e.code()
                                    )

                            } catch (
                                e: Exception
                            ) {

                                Log.e(
                                    "CHECKOUT",
                                    "Order creation failed",
                                    e
                                )

                                errorMessage =
                                    e.message
                                        ?: "Unable to create order."

                            } finally {

                                orderLoading = false
                            }
                        }
                    }
                )
            }
        }
    }
}


// =====================================================
// HEADER
// =====================================================

@Composable
private fun CheckoutHeader(
    onBack: () -> Unit
) {

    Surface(
        color = White,
        shadowElevation = 2.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = TilloraNavy
                )
            }

            Column {

                Text(
                    text = "Checkout",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = TilloraNavy
                )

                Text(
                    text = "Complete your order",
                    fontSize = 12.sp,
                    color = TilloraMuted
                )
            }
        }
    }
}


// =====================================================
// SECTION TITLE
// =====================================================

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(
                    TilloraNavy.copy(
                        alpha = 0.08f
                    )
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TilloraNavy,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TilloraText
        )
    }
}


// =====================================================
// ORDER ITEMS
// =====================================================

@Composable
private fun OrderItemsCard(
    cartItems: List<CartItem>
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = White
            ),
        border =
            BorderStroke(
                1.dp,
                TilloraBorder
            )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            cartItems.forEachIndexed { index, item ->

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(
                                RoundedCornerShape(11.dp)
                            )
                            .background(
                                TilloraNavy.copy(
                                    alpha = 0.06f
                                )
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "${item.quantity}x",
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color = TilloraNavy
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                item.product.name,
                            fontSize = 14.sp,
                            fontWeight =
                                FontWeight.SemiBold,
                            color = TilloraText,
                            maxLines = 2
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                "KES ${formatAmount(item.unitPrice)} each",
                            fontSize = 12.sp,
                            color = TilloraMuted
                        )
                    }

                    Text(
                        text =
                            "KES ${formatAmount(item.totalPrice)}",
                        fontSize = 14.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color = TilloraText
                    )
                }

                if (
                    index < cartItems.lastIndex
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    HorizontalDivider(
                        color = TilloraBorder
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )
                }
            }
        }
    }
}


// =====================================================
// DELIVERY DETAILS
// =====================================================

@Composable
private fun DeliveryDetailsCard(
    deliveryAddress: String,
    onAddressChange: (String) -> Unit,
    deliveryZones: List<DeliveryZone>,
    selectedZone: DeliveryZone?,
    onZoneSelected: (DeliveryZone) -> Unit,
    zonesLoading: Boolean,
    zonesError: String?,
    notes: String,
    onNotesChange: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = White
                ),
            border =
                BorderStroke(
                    1.dp,
                    TilloraBorder
                )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Delivery zone",
                    fontSize = 13.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color = TilloraText
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                DeliveryZoneSelector(
                    zones = deliveryZones,
                    selectedZone = selectedZone,
                    onZoneSelected =
                        onZoneSelected,
                    loading = zonesLoading
                )

                if (
                    zonesError != null
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text = zonesError,
                        fontSize = 12.sp,
                        color = TilloraRed
                    )
                }
            }
        }

        CheckoutTextField(
            value = deliveryAddress,
            onValueChange = onAddressChange,
            label = "Delivery address",
            placeholder =
                "Enter your full delivery address",
            leadingIcon =
                Icons.Outlined.LocationOn
        )

        CheckoutTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = "Order notes",
            placeholder =
                "Optional delivery instructions",
            leadingIcon =
                Icons.Outlined.Note,
            singleLine = false,
            minLines = 3
        )
    }
}


// =====================================================
// DELIVERY ZONE SELECTOR
// =====================================================

@Composable
private fun DeliveryZoneSelector(
    zones: List<DeliveryZone>,
    selectedZone: DeliveryZone?,
    onZoneSelected: (DeliveryZone) -> Unit,
    loading: Boolean
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Column {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(13.dp)
                )
                .clickable(
                    enabled =
                        !loading &&
                                zones.isNotEmpty()
                ) {
                    expanded = !expanded
                },
            color = TilloraBackground,
            shape = RoundedCornerShape(13.dp),
            border =
                BorderStroke(
                    1.dp,
                    if (expanded)
                        TilloraNavy
                    else
                        TilloraBorder
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 13.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    when {

                        loading -> {

                            Text(
                                text =
                                    "Loading delivery zones...",
                                fontSize = 14.sp,
                                color = TilloraMuted
                            )
                        }

                        selectedZone == null -> {

                            Text(
                                text =
                                    "Select your delivery zone",
                                fontSize = 14.sp,
                                color = TilloraMuted
                            )
                        }

                        else -> {

                            Text(
                                text =
                                    selectedZone.name,
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.SemiBold,
                                color = TilloraText
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )

                            Text(
                                text =
                                    "Delivery fee: KES ${formatAmount(selectedZone.fee)}",
                                fontSize = 12.sp,
                                color = TilloraNavy
                            )
                        }
                    }
                }

                if (loading) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = TilloraNavy
                    )

                } else {

                    Icon(
                        imageVector =
                            if (expanded)
                                Icons.Outlined.KeyboardArrowUp
                            else
                                Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TilloraNavy
                    )
                }
            }
        }

        if (expanded) {

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(13.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = White
                    ),
                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                border =
                    BorderStroke(
                        1.dp,
                        TilloraBorder
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(6.dp)
                ) {

                    zones.forEach { zone ->

                        Surface(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(
                                            10.dp
                                        )
                                    )
                                    .clickable {

                                        onZoneSelected(
                                            zone
                                        )

                                        expanded =
                                            false
                                    },
                            color =
                                if (
                                    selectedZone?.id ==
                                    zone.id
                                ) {
                                    TilloraNavy.copy(
                                        alpha = 0.07f
                                    )
                                } else {
                                    White
                                }
                        ) {

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Text(
                                        text =
                                            zone.name,
                                        fontSize = 14.sp,
                                        fontWeight =
                                            if (
                                                selectedZone?.id ==
                                                zone.id
                                            ) {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Medium
                                            },
                                        color =
                                            TilloraText
                                    )

                                    if (
                                        !zone.description
                                            .isNullOrBlank()
                                    ) {

                                        Spacer(
                                            modifier =
                                                Modifier.height(2.dp)
                                        )

                                        Text(
                                            text =
                                                zone.description
                                                    ?: "",
                                            fontSize = 11.sp,
                                            color =
                                                TilloraMuted,
                                            maxLines = 2
                                        )
                                    }
                                }

                                Text(
                                    text =
                                        "KES ${formatAmount(zone.fee)}",
                                    fontSize = 13.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color = TilloraNavy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// =====================================================
// TEXT FIELD
// =====================================================

@Composable
private fun CheckoutTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        placeholder = {
            Text(
                text = placeholder,
                color =
                    TilloraMuted.copy(
                        alpha = 0.7f
                    )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = TilloraNavy
            )
        },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(13.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TilloraNavy,
                unfocusedBorderColor = TilloraBorder,
                focusedLabelColor = TilloraNavy,
                cursorColor = TilloraNavy,
                focusedContainerColor = White,
                unfocusedContainerColor = White
            )
    )
}


// =====================================================
// PAYMENT OPTION
// =====================================================

@Composable
private fun PaymentOptionCard(
    selected: Boolean,
    title: String,
    subtitle: String,
    amount: Double,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            },
        color = White,
        shape = RoundedCornerShape(16.dp),
        border =
            BorderStroke(
                width =
                    if (selected)
                        1.5.dp
                    else
                        1.dp,
                color =
                    if (selected)
                        TilloraNavy
                    else
                        TilloraBorder
            )
    ) {

        Row(
            modifier =
                Modifier.padding(15.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selected,
                onClick = onClick
            )

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TilloraText
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TilloraMuted
                )
            }

            Text(
                text =
                    "KES ${formatAmount(amount)}",
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Bold,
                color = TilloraNavy
            )
        }
    }
}


// =====================================================
// ORDER SUMMARY
// =====================================================

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    tax: Double,
    deliveryFee: Double,
    total: Double
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = White
            ),
        border =
            BorderStroke(
                1.dp,
                TilloraBorder
            )
    ) {

        Column(
            modifier =
                Modifier.padding(17.dp)
        ) {

            SummaryRow(
                label = "Subtotal",
                amount = subtotal
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            SummaryRow(
                label = "Tax (16%)",
                amount = tax
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            SummaryRow(
                label = "Delivery",
                amount = deliveryFee
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            HorizontalDivider(
                color = TilloraBorder
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Total",
                    fontSize = 17.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TilloraText
                )

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Text(
                    text =
                        "KES ${formatAmount(total)}",
                    fontSize = 20.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TilloraNavy
                )
            }
        }
    }
}


// =====================================================
// SUMMARY ROW
// =====================================================

@Composable
private fun SummaryRow(
    label: String,
    amount: Double
) {

    Row(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = TilloraMuted
        )

        Spacer(
            modifier =
                Modifier.weight(1f)
        )

        Text(
            text =
                "KES ${formatAmount(amount)}",
            fontSize = 13.sp,
            fontWeight =
                FontWeight.SemiBold,
            color = TilloraText
        )
    }
}


// =====================================================
// PAYMENT REQUIRED BANNER
// =====================================================

@Composable
private fun PaymentRequiredBanner(
    amount: Double
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        color =
            TilloraNavy.copy(
                alpha = 0.06f
            ),
        shape =
            RoundedCornerShape(14.dp),
        border =
            BorderStroke(
                1.dp,
                TilloraNavy.copy(
                    alpha = 0.15f
                )
            )
    ) {

        Row(
            modifier =
                Modifier.padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier
                        .size(38.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            TilloraNavy
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.Money,
                    contentDescription = null,
                    tint = White,
                    modifier =
                        Modifier.size(19.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )

            Column {

                Text(
                    text =
                        "Order created",
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TilloraText
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        "Amount required now: KES ${formatAmount(amount)}",
                    fontSize = 12.sp,
                    color = TilloraMuted
                )
            }
        }
    }
}


// =====================================================
// PAYMENT CARD
// =====================================================

@Composable
private fun PaymentCard(
    amount: Double,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    transactionReference: String,
    onReferenceChange: (String) -> Unit,
    loading: Boolean,
    onConfirm: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = White
            ),
        border =
            BorderStroke(
                1.dp,
                TilloraBorder
            )
    ) {

        Column(
            modifier =
                Modifier.padding(17.dp)
        ) {

            Text(
                text = "Amount to pay",
                fontSize = 12.sp,
                color = TilloraMuted
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    "KES ${formatAmount(amount)}",
                fontSize = 25.sp,
                fontWeight =
                    FontWeight.Bold,
                color = TilloraNavy
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Text(
                text = "Payment method",
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color = TilloraText
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                PaymentMethodChip(
                    selected =
                        paymentMethod == "mpesa",
                    title = "M-Pesa",
                    icon =
                        Icons.Outlined.PhoneAndroid,
                    modifier =
                        Modifier.weight(1f)
                ) {
                    onPaymentMethodChange(
                        "mpesa"
                    )
                }

                PaymentMethodChip(
                    selected =
                        paymentMethod == "card",
                    title = "Card",
                    icon =
                        Icons.Outlined.CreditCard,
                    modifier =
                        Modifier.weight(1f)
                ) {
                    onPaymentMethodChange(
                        "card"
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            OutlinedTextField(
                value =
                    transactionReference,
                onValueChange =
                    onReferenceChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Transaction reference"
                    )
                },
                placeholder = {
                    Text(
                        "e.g. MPESA receipt number"
                    )
                },
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Text
                    ),
                shape =
                    RoundedCornerShape(13.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor =
                            TilloraNavy,
                        unfocusedBorderColor =
                            TilloraBorder,
                        focusedLabelColor =
                            TilloraNavy,
                        cursorColor =
                            TilloraNavy,
                        focusedContainerColor =
                            White,
                        unfocusedContainerColor =
                            White
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Button(
                onClick = onConfirm,
                enabled = !loading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape =
                    RoundedCornerShape(13.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            TilloraNavy
                    )
            ) {

                if (loading) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text =
                            "Confirm Payment",
                        fontSize = 15.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


// =====================================================
// PAYMENT METHOD CHIP
// =====================================================

@Composable
private fun PaymentMethodChip(
    selected: Boolean,
    title: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier =
            modifier
                .height(48.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .clickable {
                    onClick()
                },
        color =
            if (selected)
                TilloraNavy.copy(
                    alpha = 0.08f
                )
            else
                TilloraBackground,
        shape =
            RoundedCornerShape(12.dp),
        border =
            BorderStroke(
                1.dp,
                if (selected)
                    TilloraNavy
                else
                    TilloraBorder
            )
    ) {

        Row(
            horizontalArrangement =
                Arrangement.Center,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier =
                    Modifier.size(18.dp),
                tint = TilloraNavy
            )

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color = TilloraText
            )
        }
    }
}


// =====================================================
// BOTTOM BAR
// =====================================================

@Composable
private fun CheckoutBottomBar(
    amount: Double,
    loading: Boolean,
    enabled: Boolean,
    paymentOption: String,
    onClick: () -> Unit
) {

    Surface(
        color = White,
        shadowElevation = 8.dp
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(0.9f)
            ) {

                Text(
                    text =
                        if (
                            paymentOption ==
                            "advance"
                        ) {
                            "50% advance"
                        } else {
                            "Pay now"
                        },
                    fontSize = 12.sp,
                    color = TilloraMuted
                )

                Text(
                    text =
                        "KES ${formatAmount(amount)}",
                    fontSize = 18.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TilloraNavy
                )
            }

            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )

            Button(
                onClick = onClick,
                enabled =
                    enabled && !loading,
                modifier =
                    Modifier
                        .weight(1.4f)
                        .height(52.dp),
                shape =
                    RoundedCornerShape(13.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            TilloraNavy,
                        disabledContainerColor =
                            TilloraNavy.copy(
                                alpha = 0.35f
                            )
                    )
            ) {

                if (loading) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "Place Order",
                        fontSize = 15.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


// =====================================================
// MESSAGE CARD
// =====================================================

@Composable
private fun MessageCard(
    message: String,
    isError: Boolean
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        color =
            if (isError)
                Color(0xFFFFF1F1)
            else
                Color(0xFFEFFAF3),
        shape =
            RoundedCornerShape(12.dp)
    ) {

        Text(
            text = message,
            modifier =
                Modifier.padding(14.dp),
            fontSize = 13.sp,
            color =
                if (isError)
                    TilloraRed
                else
                    TilloraGreen,
            fontWeight =
                FontWeight.Medium
        )
    }
}


// =====================================================
// API ERROR PARSER
// =====================================================

private fun parseApiError(
    body: String?,
    statusCode: Int
): String {

    if (body.isNullOrBlank()) {
        return "Request failed: HTTP $statusCode"
    }

    return try {

        val json =
            JSONObject(body)

        // Laravel standard validation message
        val message =
            json.optString(
                "message"
            )

        if (
            message.isNotBlank()
        ) {

            val errors =
                json.optJSONObject(
                    "errors"
                )

            if (
                errors != null
            ) {

                val details =
                    errors.keys().asSequence()
                        .mapNotNull { key ->

                            val value =
                                errors.optJSONArray(
                                    key
                                )

                            if (
                                value != null &&
                                value.length() > 0
                            ) {
                                value
                                    .optString(0)
                            } else {
                                null
                            }
                        }
                        .joinToString("\n")

                if (
                    details.isNotBlank()
                ) {
                    return "$message\n$details"
                }
            }

            return message
        }

        body

    } catch (
        e: Exception
    ) {

        Log.e(
            "CHECKOUT",
            "Could not parse API error",
            e
        )

        body
    }
}


// =====================================================
// MONEY FORMAT
// =====================================================

private fun formatAmount(
    amount: Double
): String {

    return String.format(
        Locale.US,
        "%,.2f",
        amount
    )
}