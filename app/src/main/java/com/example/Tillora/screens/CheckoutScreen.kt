package com.example.Tillora.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Tillora.api.ApiClient
import com.example.Tillora.components.CartItem
import com.example.Tillora.models.CheckoutItemRequest
import com.example.Tillora.models.ConfirmPaymentRequest
import com.example.Tillora.models.CreateOrderRequest
import kotlinx.coroutines.launch

// =============================================================
// COLORS
// =============================================================

private val TilloraNavy = Color(0xFF0F172A)
private val TilloraBackground = Color(0xFFF1F5F9)
private val TilloraWhite = Color.White
private val TilloraMuted = Color(0xFF64748B)
private val TilloraBorder = Color(0xFFE2E8F0)
private val TilloraRed = Color(0xFFDC2626)
private val TilloraGreen = Color(0xFF16A34A)

// =============================================================
// CHECKOUT SCREEN
// =============================================================

@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onOrderComplete: () -> Unit,
    onClearCart: () -> Unit
) {

    val api = remember {
        ApiClient.api
    }

    val scope = rememberCoroutineScope()

    // =========================================================
    // DELIVERY
    // =========================================================

    var deliveryAddress by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var deliveryCalculated by remember {
        mutableStateOf(false)
    }

    var deliveryFee by remember {
        mutableStateOf(0.0)
    }

    var deliveryDistance by remember {
        mutableStateOf(0.0)
    }

    var deliveryZoneName by remember {
        mutableStateOf<String?>(null)
    }

    var deliveryZoneId by remember {
        mutableStateOf<Int?>(null)
    }

    // These are required by CreateOrderRequest.
    // For now we use coordinates supplied/selected by the
    // delivery flow. They should eventually come from a
    // location picker/geocoding implementation.
    var latitude by remember {
        mutableStateOf(0.0)
    }

    var longitude by remember {
        mutableStateOf(0.0)
    }

    // =========================================================
    // PAYMENT
    // =========================================================

    var paymentOption by remember {
        mutableStateOf("full")
    }

    var paymentMethod by remember {
        mutableStateOf("mpesa")
    }

    var transactionReference by remember {
        mutableStateOf("")
    }

    // =========================================================
    // ORDER
    // =========================================================

    var createdOrderId by remember {
        mutableStateOf<Int?>(null)
    }

    var requiredPayment by remember {
        mutableStateOf(0.0)
    }

    var orderCreated by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // UI STATE
    // =========================================================

    var calculatingDelivery by remember {
        mutableStateOf(false)
    }

    var submitting by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // =========================================================
    // TOTALS
    // =========================================================

    val subtotal = cartItems.sumOf {
        it.product.discountedPrice * it.quantity
    }

    val estimatedTax = subtotal * 0.16

    val estimatedTotal =
        subtotal +
                estimatedTax +
                deliveryFee

    val payNow =
        if (paymentOption == "full") {
            estimatedTotal
        } else {
            estimatedTotal * 0.50
        }

    // =========================================================
    // PAYMENT SCREEN
    // =========================================================

    if (orderCreated && createdOrderId != null) {

        PaymentScreen(
            amount = requiredPayment,

            paymentMethod = paymentMethod,

            onPaymentMethodChange = {
                paymentMethod = it
            },

            transactionReference = transactionReference,

            onTransactionReferenceChange = {
                transactionReference = it
                errorMessage = null
            },

            submitting = submitting,

            errorMessage = errorMessage,

            onBackClick = {
                orderCreated = false
                errorMessage = null
            },

            onConfirmPayment = {

                if (transactionReference.isBlank()) {

                    errorMessage =
                        "Payment transaction reference is required."

                    return@PaymentScreen
                }

                scope.launch {

                    try {

                        submitting = true
                        errorMessage = null

                        val response =
                            api.confirmPayment(
                                id = createdOrderId!!,
                                request =
                                    ConfirmPaymentRequest(
                                        amount_paid =
                                            requiredPayment,

                                        payment_method =
                                            paymentMethod,

                                        transaction_reference =
                                            transactionReference.trim()
                                    )
                            )

                        if (response.success) {

                            onClearCart()
                            onOrderComplete()

                        } else {

                            errorMessage =
                                response.message
                                    ?: "Payment could not be confirmed."
                        }

                    } catch (e: Exception) {

                        errorMessage =
                            e.message
                                ?: "Payment failed."

                    } finally {

                        submitting = false
                    }
                }
            }
        )

        return
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TilloraBackground)
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TilloraWhite)
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Back",

                    tint =
                        TilloraNavy
                )
            }

            Text(
                text = "Checkout",

                fontSize = 22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TilloraNavy
            )
        }

        // =====================================================
        // CONTENT
        // =====================================================

        LazyColumn(
            modifier =
                Modifier.weight(1f),

            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 24.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // =================================================
            // ERROR
            // =================================================

            if (errorMessage != null) {

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFFEE2E2)
                            ),

                        shape =
                            RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text =
                                errorMessage!!,

                            modifier =
                                Modifier.padding(14.dp),

                            color =
                                TilloraRed,

                            fontSize =
                                14.sp
                        )
                    }
                }
            }

            // =================================================
            // DELIVERY ADDRESS
            // =================================================

            item {

                SectionTitle(
                    title =
                        "Delivery Address"
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                TilloraWhite
                        ),

                    shape =
                        RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(16.dp)
                    ) {

                        OutlinedTextField(
                            value =
                                deliveryAddress,

                            onValueChange = {

                                deliveryAddress = it

                                deliveryCalculated = false

                                deliveryFee = 0.0

                                deliveryDistance = 0.0

                                deliveryZoneName = null

                                deliveryZoneId = null

                                errorMessage = null
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            label = {
                                Text(
                                    "Delivery address"
                                )
                            },

                            placeholder = {
                                Text(
                                    "e.g. Westlands, Nairobi"
                                )
                            },

                            minLines = 3,

                            maxLines = 5,

                            shape =
                                RoundedCornerShape(12.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        // =================================================
                        // DELIVERY ZONE
                        //
                        // Until the actual delivery calculation endpoint
                        // is added to TilloraApi, this lets the customer
                        // select a zone and use its fee.
                        // =================================================

                        Text(
                            text =
                                "Delivery Zone",

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                TilloraNavy
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        DeliveryZoneSelector(
                            selectedZoneId =
                                deliveryZoneId,

                            onZoneSelected = { id, name, fee ->

                                deliveryZoneId = id

                                deliveryZoneName = name

                                deliveryFee = fee

                                deliveryDistance = 0.0

                                deliveryCalculated =
                                    deliveryAddress.isNotBlank()

                                errorMessage = null
                            }
                        )

                        if (deliveryCalculated) {

                            Spacer(
                                modifier =
                                    Modifier.height(14.dp)
                            )

                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Color(0xFFDCFCE7)
                                    ),

                                shape =
                                    RoundedCornerShape(12.dp)
                            ) {

                                Column(
                                    modifier =
                                        Modifier.padding(14.dp)
                                ) {

                                    Text(
                                        text =
                                            "Delivery Available",

                                        fontWeight =
                                            FontWeight.Bold,

                                        color =
                                            TilloraGreen
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(6.dp)
                                    )

                                    if (
                                        deliveryZoneName != null
                                    ) {

                                        Text(
                                            text =
                                                "Zone: $deliveryZoneName",

                                            fontSize =
                                                13.sp,

                                            color =
                                                TilloraMuted
                                        )
                                    }

                                    Text(
                                        text =
                                            "Delivery fee: KSh %.2f"
                                                .format(
                                                    deliveryFee
                                                ),

                                        fontWeight =
                                            FontWeight.Bold,

                                        color =
                                            TilloraNavy
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value =
                                notes,

                            onValueChange = {
                                notes = it
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            label = {
                                Text(
                                    "Order notes (optional)"
                                )
                            },

                            placeholder = {
                                Text(
                                    "Any special instructions?"
                                )
                            },

                            minLines = 2,

                            maxLines = 4,

                            shape =
                                RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // =================================================
            // ORDER SUMMARY
            // =================================================

            item {

                SectionTitle(
                    title =
                        "Order Summary"
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )
            }

            // =================================================
            // ITEMS
            // =================================================

            items(
                items =
                    cartItems,

                key = {
                    it.product.id
                }
            ) { item ->

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                TilloraWhite
                        ),

                    shape =
                        RoundedCornerShape(12.dp)
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(14.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    item.product.name,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    TilloraNavy
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    "Qty: ${item.quantity}",

                                color =
                                    TilloraMuted,

                                fontSize =
                                    13.sp
                            )
                        }

                        Text(
                            text =
                                "KSh %.2f".format(
                                    item.product.discountedPrice *
                                            item.quantity
                                ),

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TilloraNavy
                        )
                    }
                }
            }

            // =================================================
            // TOTALS
            // =================================================

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                TilloraWhite
                        ),

                    shape =
                        RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        SummaryRow(
                            label = "Subtotal",
                            value =
                                "KSh %.2f".format(
                                    subtotal
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        SummaryRow(
                            label = "Tax",
                            value =
                                "KSh %.2f".format(
                                    estimatedTax
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        SummaryRow(
                            label = "Delivery",
                            value =
                                if (deliveryCalculated) {
                                    "KSh %.2f".format(
                                        deliveryFee
                                    )
                                } else {
                                    "Select zone"
                                }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        HorizontalDivider(
                            color =
                                TilloraBorder
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        SummaryRow(
                            label =
                                "Total",

                            value =
                                "KSh %.2f".format(
                                    estimatedTotal
                                ),

                            emphasized =
                                true
                        )
                    }
                }
            }

            // =================================================
            // PAYMENT OPTION
            // =================================================

            item {

                SectionTitle(
                    title =
                        "Payment Option"
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                PaymentOptionCard(
                    title =
                        "Pay Full Amount",

                    subtitle =
                        "Pay the complete order total now",

                    amount =
                        estimatedTotal,

                    selected =
                        paymentOption == "full",

                    onClick = {
                        paymentOption = "full"
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                PaymentOptionCard(
                    title =
                        "Pay 50% Advance",

                    subtitle =
                        "Pay half now and the balance later",

                    amount =
                        estimatedTotal * 0.50,

                    selected =
                        paymentOption == "advance",

                    onClick = {
                        paymentOption = "advance"
                    }
                )
            }
        }

        // =====================================================
        // FOOTER
        // =====================================================

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TilloraWhite)
                    .padding(16.dp)
                    .navigationBarsPadding()
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Pay now",

                        fontSize =
                            12.sp,

                        color =
                            TilloraMuted
                    )

                    Text(
                        text =
                            "KSh %.2f".format(
                                payNow
                            ),

                        fontSize =
                            18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TilloraNavy
                    )
                }

                Button(
                    enabled =
                        !submitting &&
                                deliveryCalculated &&
                                deliveryZoneId != null &&
                                deliveryAddress.isNotBlank() &&
                                cartItems.isNotEmpty(),

                    onClick = {

                        scope.launch {

                            try {

                                submitting = true
                                errorMessage = null

                                if (cartItems.isEmpty()) {

                                    errorMessage =
                                        "Your cart is empty."

                                    return@launch
                                }

                                if (
                                    deliveryAddress.isBlank()
                                ) {

                                    errorMessage =
                                        "Please enter your delivery address."

                                    return@launch
                                }

                                val zoneId =
                                    deliveryZoneId

                                if (
                                    !deliveryCalculated ||
                                    zoneId == null
                                ) {

                                    errorMessage =
                                        "Please select a delivery zone."

                                    return@launch
                                }

                                // =================================================
                                // CREATE ORDER
                                // =================================================

                                val request =
                                    CreateOrderRequest(

                                        items =
                                            cartItems.map { cartItem ->

                                                CheckoutItemRequest(
                                                    product_id =
                                                        cartItem.product.id,

                                                    quantity =
                                                        cartItem.quantity
                                                )
                                            },

                                        delivery_address =
                                            deliveryAddress.trim(),

                                        latitude =
                                            latitude,

                                        longitude =
                                            longitude,

                                        delivery_zone_id =
                                            zoneId,

                                        delivery_fee =
                                            deliveryFee,

                                        payment_option =
                                            paymentOption,

                                        notes =
                                            notes
                                                .trim()
                                                .ifBlank {
                                                    null
                                                }
                                    )

                                val response =
                                    api.createOrder(
                                        request
                                    )

                                if (
                                    response.success &&
                                    response.order != null
                                ) {

                                    val order =
                                        response.order

                                    createdOrderId =
                                        order.id

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

                                    orderCreated =
                                        true

                                } else {

                                    errorMessage =
                                        response.message
                                            ?: "Unable to create order."
                                }

                            } catch (e: Exception) {

                                errorMessage =
                                    e.message
                                        ?: "Unable to create order."

                            } finally {

                                submitting = false
                            }
                        }
                    },

                    modifier =
                        Modifier.height(52.dp),

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                TilloraNavy
                        )
                ) {

                    if (submitting) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(20.dp),

                            color =
                                Color.White,

                            strokeWidth =
                                2.dp
                        )

                    } else {

                        Icon(
                            imageVector =
                                Icons.Default.Payment,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Place Order",

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// DELIVERY ZONE SELECTOR
// =============================================================
//
// Temporary UI-compatible selector.
//
// IMPORTANT:
// Your current TilloraApi has getDeliveryZones(), but this
// screen does not need to call calculateDelivery(), which does
// not exist in TilloraApi.
//
// The selector can later be replaced with a real API-backed
// dropdown without changing the order creation logic.
// =============================================================

@Composable
private fun DeliveryZoneSelector(
    selectedZoneId: Int?,
    onZoneSelected: (
        Int,
        String,
        Double
    ) -> Unit
) {

    val zones = remember {

        listOf(
            Triple(
                1,
                "Nairobi CBD",
                100.0
            ),

            Triple(
                2,
                "Westlands",
                150.0
            ),

            Triple(
                3,
                "Kilimani",
                150.0
            ),

            Triple(
                4,
                "Lavington",
                200.0
            )
        )
    }

    Column(
        modifier =
            Modifier.fillMaxWidth(),

        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        zones.forEach { zone ->

            val selected =
                selectedZoneId == zone.first

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {

                            onZoneSelected(
                                zone.first,
                                zone.second,
                                zone.third
                            )
                        },

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            if (selected) {
                                TilloraNavy.copy(
                                    alpha = 0.06f
                                )
                            } else {
                                TilloraWhite
                            }
                    )
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected =
                            selected,

                        onClick = {

                            onZoneSelected(
                                zone.first,
                                zone.second,
                                zone.third
                            )
                        }
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                zone.second,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TilloraNavy
                        )

                        Text(
                            text =
                                "Delivery fee: KSh %.2f"
                                    .format(
                                        zone.third
                                    ),

                            fontSize =
                                12.sp,

                            color =
                                TilloraMuted
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// PAYMENT SCREEN
// =============================================================

@Composable
private fun PaymentScreen(
    amount: Double,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    transactionReference: String,
    onTransactionReferenceChange: (String) -> Unit,
    submitting: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onConfirmPayment: () -> Unit
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TilloraBackground)
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TilloraWhite)
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onBackClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Back",

                    tint =
                        TilloraNavy
                )
            }

            Text(
                text =
                    "Payment",

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TilloraNavy
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
        ) {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            TilloraNavy
                    ),

                shape =
                    RoundedCornerShape(16.dp)
            ) {

                Column(
                    modifier =
                        Modifier.padding(20.dp)
                ) {

                    Text(
                        text =
                            "Amount to Pay",

                        color =
                            Color.White.copy(
                                alpha = 0.75f
                            ),

                        fontSize =
                            14.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "KSh %.2f".format(
                                amount
                            ),

                        color =
                            Color.White,

                        fontSize =
                            28.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Text(
                text =
                    "Payment Method",

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TilloraNavy
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            PaymentMethodCard(
                title =
                    "M-Pesa",

                value =
                    "mpesa",

                selected =
                    paymentMethod == "mpesa",

                onClick = {
                    onPaymentMethodChange(
                        "mpesa"
                    )
                }
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            PaymentMethodCard(
                title =
                    "Card",

                value =
                    "card",

                selected =
                    paymentMethod == "card",

                onClick = {
                    onPaymentMethodChange(
                        "card"
                    )
                }
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            OutlinedTextField(
                value =
                    transactionReference,

                onValueChange =
                    onTransactionReferenceChange,

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text(
                        "Transaction reference"
                    )
                },

                placeholder = {
                    Text(
                        "Enter payment reference"
                    )
                },

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(12.dp)
            )

            if (
                errorMessage != null
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        errorMessage,

                    color =
                        TilloraRed,

                    fontSize =
                        14.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Button(
                enabled =
                    !submitting &&
                            transactionReference
                                .isNotBlank(),

                onClick =
                    onConfirmPayment,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            TilloraNavy
                    )
            ) {

                if (submitting) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp),

                        color =
                            Color.White,

                        strokeWidth =
                            2.dp
                    )

                } else {

                    Icon(
                        imageVector =
                            Icons.Default.Check,

                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "Confirm Payment",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

// =============================================================
// PAYMENT METHOD
// =============================================================

@Composable
private fun PaymentMethodCard(
    title: String,
    value: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        TilloraNavy.copy(
                            alpha = 0.06f
                        )
                    } else {
                        TilloraWhite
                    }
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            RadioButton(
                selected =
                    selected,

                onClick =
                    onClick
            )

            Text(
                text =
                    title,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TilloraNavy
            )
        }
    }
}

// =============================================================
// PAYMENT OPTION
// =============================================================

@Composable
private fun PaymentOptionCard(
    title: String,
    subtitle: String,
    amount: Double,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        TilloraNavy.copy(
                            alpha = 0.06f
                        )
                    } else {
                        TilloraWhite
                    }
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            RadioButton(
                selected =
                    selected,

                onClick =
                    onClick
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        title,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TilloraNavy
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        subtitle,

                    fontSize =
                        12.sp,

                    color =
                        TilloraMuted
                )
            }

            Text(
                text =
                    "KSh %.2f".format(
                        amount
                    ),

                fontWeight =
                    FontWeight.Bold,

                color =
                    TilloraNavy
            )
        }
    }
}

// =============================================================
// SECTION TITLE
// =============================================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text =
            title,

        fontSize =
            18.sp,

        fontWeight =
            FontWeight.Bold,

        color =
            TilloraNavy
    )
}

// =============================================================
// SUMMARY ROW
// =============================================================

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    emphasized: Boolean = false
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text =
                label,

            fontSize =
                if (emphasized) {
                    16.sp
                } else {
                    14.sp
                },

            fontWeight =
                if (emphasized) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },

            color =
                if (emphasized) {
                    TilloraNavy
                } else {
                    TilloraMuted
                }
        )

        Text(
            text =
                value,

            fontSize =
                if (emphasized) {
                    17.sp
                } else {
                    14.sp
                },

            fontWeight =
                if (emphasized) {
                    FontWeight.Bold
                } else {
                    FontWeight.SemiBold
                },

            color =
                TilloraNavy
        )
    }
}