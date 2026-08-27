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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Tillora.viewmodel.AuthViewModel

@Composable
fun OtpVerificationScreen(
    onBackClick: () -> Unit,
    onVerificationSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {

    var otp by remember {
        mutableStateOf("")
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val navy = Color(0xFF0B1F3A)
    val background = Color(0xFFF1F5F9)
    val secondaryText = Color(0xFF64748B)

    val email = viewModel.getPendingEmail()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 24.dp)
    ) {

        // =====================================================
        // BACK BUTTON
        // =====================================================

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

        // =====================================================
        // CONTENT
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "✉️",
                    fontSize = 40.sp
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Verify Your Email",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = navy
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "We sent a 6-digit verification code to:",
                fontSize = 14.sp,
                color = secondaryText
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = email,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =================================================
            // OTP
            // =================================================

            Text(
                text = "Verification Code",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = navy
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = otp,
                onValueChange = {

                    if (it.length <= 6 && it.all { char ->
                            char.isDigit()
                        }) {
                        otp = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Enter 6-digit code")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
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

            // =================================================
            // ERROR
            // =================================================

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

            // =================================================
            // VERIFY BUTTON
            // =================================================

            Button(
                onClick = {

                    viewModel.verifyOtp(
                        otp = otp,
                        onSuccess = {

                            viewModel.completeRegistration(
                                onSuccess = onVerificationSuccess
                            )
                        }
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
                enabled = !isLoading && otp.length == 6
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "Verify Email",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // =================================================
            // RESEND
            // =================================================

            TextButton(
                onClick = {
                    viewModel.resendOtp()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {

                Text(
                    text = "Didn't receive the code? Resend",
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