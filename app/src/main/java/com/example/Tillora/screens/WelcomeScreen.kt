package com.example.Tillora.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit
) {

    val images = listOf(

        "https://images.unsplash.com/photo-1441986300917-64674bd600d8",

        "https://images.unsplash.com/photo-1523275335684-37898b6baf30",

        "https://images.unsplash.com/photo-1542291026-7eec264c27ff",

        "https://images.unsplash.com/photo-1490481651871-ab68de25d43d",

        "https://images.unsplash.com/photo-1525507119028-ed4c629a60a3"
    )

    val listState = rememberLazyListState()

    val currentPage by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    LaunchedEffect(Unit) {

        while (true) {

            delay(4000)

            val nextPage =
                if (currentPage >= images.lastIndex) {
                    0
                } else {
                    currentPage + 1
                }

            listState.animateScrollToItem(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFE9EDF2)
            )
            .padding(
                horizontal = 18.dp
            )
    ) {

        // -------------------------------------------------
        // BRAND
        // -------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 88.dp,
                    bottom = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(
                        Color(0xFF0B1F3A)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "🏪",
                    fontSize = 32.sp
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Tillora",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1F3A)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Shop smarter. Live better.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }

        // -------------------------------------------------
        // LARGE RECTANGULAR IMAGE CAROUSEL
        // -------------------------------------------------

        LazyRow(
            state = listState,

            modifier = Modifier
                .fillMaxWidth()
                .height(390.dp),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            itemsIndexed(images) { _, imageUrl ->

                AsyncImage(
                    model = imageUrl,

                    contentDescription =
                        "Tillora shopping",

                    modifier = Modifier
                        .fillParentMaxWidth(0.96f)
                        .height(390.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        ),

                    contentScale =
                        ContentScale.Crop
                )
            }
        }

        // -------------------------------------------------
        // DOTS
        // -------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 14.dp
                ),

            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            images.forEachIndexed { index, _ ->

                Box(
                    modifier = Modifier
                        .padding(
                            horizontal = 4.dp
                        )
                        .size(
                            if (index == currentPage)
                                9.dp
                            else
                                7.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            if (index == currentPage)
                                Color(0xFF0B1F3A)
                            else
                                Color(0xFFB8C0CA)
                        )
                )
            }
        }

        // -------------------------------------------------
        // SMALL GAP
        // -------------------------------------------------

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // -------------------------------------------------
        // LOGIN
        // -------------------------------------------------

        Button(
            onClick = onLoginClick,

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor =
                    Color(0xFF0B1F3A),

                contentColor =
                    Color.White
            )
        ) {

            Text(
                text = "Login",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}