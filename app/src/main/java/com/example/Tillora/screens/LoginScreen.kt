package com.example.Tillora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
fun LoginScreen(
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val navy = Color(0xFF0B1F3A)
    val background = Color(0xFFF1F5F9)
    val secondaryText = Color(0xFF64748B)

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
            modifier = Modifier.padding(top = 16.dp)
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = navy
            )
        }

        // =========================================================
        // CENTERED CONTENT
        // =========================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            // -----------------------------------------------------
            // BRAND
            // -----------------------------------------------------

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🏪",
                    fontSize = 38.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Tillora",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = navy
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Shop smarter. Live better.",
                    fontSize = 13.sp,
                    color = secondaryText
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // -----------------------------------------------------
            // TITLE
            // -----------------------------------------------------

            Text(
                text = "Welcome Back",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Login to your Tillora account",
                fontSize = 14.sp,
                color = secondaryText
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // -----------------------------------------------------
            // EMAIL
            // -----------------------------------------------------

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
                    focusedLabelColor = navy,
                    cursorColor = navy
                )
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // -----------------------------------------------------
            // PASSWORD
            // -----------------------------------------------------

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
                    focusedLabelColor = navy,
                    cursorColor = navy
                )
            )

            // -----------------------------------------------------
            // ERROR
            // -----------------------------------------------------

            if (error != null) {

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

            // -----------------------------------------------------
            // LOGIN BUTTON
            // -----------------------------------------------------

            Button(
                onClick = {

                    viewModel.login(
                        email = email.trim(),
                        password = password,
                        onSuccess = onLoginSuccess
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
                        email.isNotBlank() &&
                        password.isNotBlank()
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -----------------------------------------------------
            // REGISTER
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Don't have an account?",
                    color = secondaryText,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                TextButton(
                    onClick = onRegisterClick,
                    enabled = !isLoading
                ) {

                    Text(
                        text = "Register",
                        color = navy,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}