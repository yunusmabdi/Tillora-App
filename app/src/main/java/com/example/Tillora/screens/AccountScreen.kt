package com.example.Tillora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Tillora.viewmodel.AuthViewModel

@Composable
fun AccountScreen(
    authViewModel: AuthViewModel,
    onEditProfileClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val customer by authViewModel.customer.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EDF2))
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {

        // -------------------------------------------------
        // HEADER
        // -------------------------------------------------

        Text(
            text = "My Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B1F3A),
            modifier = Modifier.padding(
                top = 35.dp,
                bottom = 8.dp
            )
        )

        // -------------------------------------------------
        // PROFILE CARD
        // -------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0B1F3A)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = customer?.name ?: "Tillora Customer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = customer?.email ?: "customer@tillora.com",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit profile",
                tint = Color(0xFF0B1F3A),
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        onEditProfileClick()
                    }
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // -------------------------------------------------
        // ACCOUNT
        // -------------------------------------------------

        Text(
            text = "Account",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        AccountOption(
            icon = Icons.Default.ReceiptLong,
            title = "My Orders",
            subtitle = "View your order history",
            onClick = onOrdersClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        AccountOption(
            icon = Icons.Default.Edit,
            title = "Edit Profile",
            subtitle = "Update your account details",
            onClick = onEditProfileClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        AccountOption(
            icon = Icons.Default.HelpOutline,
            title = "Help & Support",
            subtitle = "Get help with your orders",
            onClick = onHelpClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        AccountOption(
            icon = Icons.Default.Info,
            title = "About Tillora",
            subtitle = "Learn more about Tillora",
            onClick = onAboutClick
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        // -------------------------------------------------
        // LOGOUT
        // -------------------------------------------------

        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B1F3A),
                contentColor = Color.White
            )
        ) {

            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout"
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = "Logout",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Tillora",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


// =====================================================
// ACCOUNT OPTION
// =====================================================

@Composable
private fun AccountOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE9EDF2)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF0B1F3A),
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8)
        )
    }
}