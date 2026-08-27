package com.example.Tillora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Tillora.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    onOtpSent: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {

    // =========================================================
    // FORM STATE
    // =========================================================

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    // =========================================================
    // VIEWMODEL STATE
    // =========================================================

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // =========================================================
    // COLORS
    // =========================================================

    val navy = Color(0xFF0B1F3A)
    val background = Color(0xFFF1F5F9)
    val secondaryText = Color(0xFF64748B)

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 24.dp)
    ) {

        // =========================================================
        // BACK BUTTON
        // =========================================================

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp),
            enabled = !isLoading
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = navy
            )
        }

        // =========================================================
        // CENTER CONTENT
        // =========================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            // =====================================================
            // BRAND
            // =====================================================

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🏪",
                    fontSize = 34.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Tillora",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = navy
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================================
            // TITLE
            // =====================================================

            Text(
                text = "Create Account",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Create your Tillora account",
                fontSize = 14.sp,
                color = secondaryText
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================================
            // FULL NAME
            // =====================================================

            Text(
                text = "Full Name",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Enter your name")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = navy,
                    unfocusedBorderColor = navy,
                    focusedLeadingIconColor = navy,
                    unfocusedLeadingIconColor = secondaryText,
                    cursorColor = navy
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================================
            // EMAIL
            // =====================================================

            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Enter your email")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = navy,
                    unfocusedBorderColor = navy,
                    focusedLeadingIconColor = navy,
                    unfocusedLeadingIconColor = secondaryText,
                    cursorColor = navy
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================================
            // PHONE
            // =====================================================

            Text(
                text = "Phone",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Enter your phone number")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = navy,
                    unfocusedBorderColor = navy,
                    focusedLeadingIconColor = navy,
                    unfocusedLeadingIconColor = secondaryText,
                    cursorColor = navy
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================================
            // PASSWORD
            // =====================================================

            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Enter your password")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = navy,
                    unfocusedBorderColor = navy,
                    focusedLeadingIconColor = navy,
                    unfocusedLeadingIconColor = secondaryText,
                    cursorColor = navy
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================================
            // CONFIRM PASSWORD
            // =====================================================

            Text(
                text = "Confirm Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Confirm your password")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = navy,
                    unfocusedBorderColor = navy,
                    focusedLeadingIconColor = navy,
                    unfocusedLeadingIconColor = secondaryText,
                    cursorColor = navy
                )
            )

            // =====================================================
            // ERROR
            // =====================================================

            if (!error.isNullOrBlank()) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = error ?: "",
                    color = Color(0xFFDC2626),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // =====================================================
            // SEND VERIFICATION CODE
            // =====================================================

            Button(
                onClick = {

                    viewModel.sendOtp(
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim().ifBlank { null },
                        password = password,
                        passwordConfirmation = confirmPassword,
                        onSuccess = onOtpSent
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = navy,
                    contentColor = Color.White
                ),
                enabled = !isLoading &&
                        name.isNotBlank() &&
                        email.isNotBlank() &&
                        password.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        password == confirmPassword
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "Send Verification Code",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // =====================================================
            // LOGIN
            // =====================================================

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {

                Text(
                    text = "Already have an account? Login",
                    color = navy,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}