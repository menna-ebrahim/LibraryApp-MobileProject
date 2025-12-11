package com.example.loginregistration.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

class BookDetailsActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Get Data passed from MainActivity
        val title = intent.getStringExtra("TITLE") ?: "No Title"
        val author = intent.getStringExtra("AUTHOR") ?: "Unknown"
        val description = intent.getStringExtra("DESC") ?: "No description available."
        val img = intent.getStringExtra("IMG") ?: ""

        setContent {
            MaterialTheme {
                Scaffold(
                    containerColor = Color(0xFFF9F5F0), // Cream Background
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    "Book Details",
                                    fontFamily = FontFamily.Companion.Serif,
                                    fontWeight = FontWeight.Companion.Bold,
                                    color = Color(0xFF4A3B32)
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        "Back",
                                        tint = Color(0xFF4A3B32)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color(
                                    0xFFF9F5F0
                                )
                            )
                        )
                    },
                    bottomBar = {
                        // The "Preview" Button at the bottom
                        Button(
                            onClick = { /* TODO: Read logic */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD98E73)), // Brown/Orange
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(24.dp)
                                .height(56.dp)
                        ) {
                            Text(
                                "Preview",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Companion.Bold,
                                color = Color.Companion.White
                            )
                        }
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier.Companion
                            .padding(padding)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Companion.CenterHorizontally
                    ) {

                        // --- 1. The Shelf Display ---
                        Box(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .height(280.dp),
                            contentAlignment = Alignment.Companion.BottomCenter
                        ) {
                            // Shelf background
                            Box(
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .background(Color(0xFFC48E68)) // Wood color
                            )

                            // The Book Cover
                            Card(
                                elevation = CardDefaults.cardElevation(12.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                modifier = Modifier.Companion
                                    .width(160.dp)
                                    .height(240.dp)
                                    .padding(bottom = 0.dp) // Sit on the shelf
                            ) {
                                AsyncImage(
                                    model = img,
                                    contentDescription = title,
                                    contentScale = ContentScale.Companion.Crop,
                                    modifier = Modifier.Companion.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.Companion.height(24.dp))

                        // --- 2. Title & Author ---
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 28.sp,
                                fontFamily = FontFamily.Companion.Serif,
                                fontWeight = FontWeight.Companion.Bold,
                                color = Color(0xFF4A3B32)
                            )
                            Text(
                                text = "by $author",
                                fontSize = 18.sp,
                                color = Color.Companion.Gray,
                                modifier = Modifier.Companion.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.Companion.height(16.dp))

                            // --- 3. Rating Row ---
                            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                                // 5 Static Stars for design
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107), // Gold
                                        modifier = Modifier.Companion.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.Companion.width(8.dp))
                                Text(
                                    "4.8",
                                    fontWeight = FontWeight.Companion.Bold,
                                    color = Color(0xFF4A3B32)
                                )
                                Text(
                                    " (120 Reviews)",
                                    color = Color.Companion.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.Companion.height(24.dp))

                            // --- 4. Description ---
                            Text(
                                text = description,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFF5D4037) // Darker brown text
                            )

                            // Add extra space at bottom for scrolling above the button
                            Spacer(modifier = Modifier.Companion.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}