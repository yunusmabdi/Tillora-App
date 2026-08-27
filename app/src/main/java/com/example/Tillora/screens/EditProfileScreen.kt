package com.example.Tillora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Tillora.models.Customer
import com.example.Tillora.viewmodel.AuthViewModel

@Composable
fun EditProfileScreen(
    customer: Customer,
    viewModel: AuthViewModel,
    onCancelClick: () -> Unit
) {

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var name by remember(customer) {
        mutableStateOf(customer.name)
    }

    var phone by remember(customer) {
        mutableStateOf(customer.phone ?: "")
    }

    var address by remember(customer) {
        mutableStateOf(customer.address ?: "")
    }

    var city by remember(customer) {
        mutableStateOf(customer.city ?: "")
    }

    var country by remember(customer) {
        mutableStateOf(customer.country ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EDF2))
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 36.dp,
                    bottom = 10.dp
                )
        ) {

            Text(
                text = "Edit Profile",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1F3A)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Update your personal information",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }

        // =====================================================
        // FORM
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 30.dp
                )
        ) {

            // =================================================
            // NAME
            // =================================================

            ProfileField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = "Full Name"
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =================================================
            // EMAIL
            // =================================================

            ProfileField(
                value = customer.email,
                onValueChange = {},
                label = "Email",
                enabled = false
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =================================================
            // PHONE
            // =================================================

            ProfileField(
                value = phone,
                onValueChange = {
                    phone = it
                },
                label = "Phone Number"
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =================================================
            // ADDRESS
            // =================================================

            ProfileField(
                value = address,
                onValueChange = {
                    address = it
                },
                label = "Address",
                singleLine = false
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =================================================
            // CITY
            // =================================================

            ProfileField(
                value = city,
                onValueChange = {
                    city = it
                },
                label = "City"
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =================================================
            // COUNTRY
            // =================================================

            ProfileField(
                value = country,
                onValueChange = {
                    country = it
                },
                label = "Country"
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =================================================
            // ERROR
            // =================================================

            if (!error.isNullOrBlank()) {

                Text(
                    text = error ?: "",
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(
                        bottom = 12.dp
                    )
                )
            }

            // =================================================
            // SAVE BUTTON
            // =================================================

            Button(
                onClick = {

                    viewModel.updateProfile(
                        name = name.trim(),
                        phone = phone.trim().ifBlank { null },
                        address = address.trim().ifBlank { null },
                        city = city.trim().ifBlank { null },
                        country = country.trim().ifBlank { null },
                        onSuccess = onCancelClick
                    )
                },

                enabled = !isLoading && name.isNotBlank(),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B1F3A),
                    contentColor = Color.White
                )
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                    Spacer(
                        modifier = Modifier.height(0.dp)
                    )

                    Text(
                        text = "Saving..."
                    )

                } else {

                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save"
                    )

                    Spacer(
                        modifier = Modifier.height(0.dp)
                    )

                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // CANCEL BUTTON
            // =================================================

            OutlinedButton(
                onClick = onCancelClick,

                enabled = !isLoading,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// =============================================================
// PROFILE FIELD
// =============================================================

@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        label = {
            Text(label)
        },

        enabled = enabled,

        singleLine = singleLine,

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(14.dp)
    )
}