package com.example.loginregistration.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.LibraryViewModel

class BookDetailsActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize ViewModel
        val viewModel = LibraryViewModel(application)

        // 2. Get Data passed from HomeActivity
        val bookId = intent.getIntExtra("BOOK_ID", -1) // <--- GET THE ID
        val title = intent.getStringExtra("TITLE") ?: "No Title"
        val author = intent.getStringExtra("AUTHOR") ?: "Unknown"
        val description = intent.getStringExtra("DESC") ?: "No description available."
        val img = intent.getStringExtra("IMG") ?: ""

        setContent {
            val context = LocalContext.current

            MaterialTheme {
                Scaffold(
                    containerColor = Color(0xFFF9F5F0), // Cream Background
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    "Book Details",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
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
                                containerColor = Color(0xFFF9F5F0)
                            )
                        )
                    },
                    // --- 3. ADD FLOATING ACTION BUTTON HERE ---
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                if (bookId != -1) {
                                    // Reconstruct a temporary Book object to pass to the ViewModel
                                    // (The ViewModel needs a Book object, even though we mainly need the ID)
                                    val bookToAdd = Book(
                                        id = bookId,
                                        title = title,
                                        author = author,
                                        description = description,
                                        img = img,
                                        Cat_Id = 0, // Not needed for adding to library
                                        categoryName = "" // Not needed for adding to library
                                    )

                                    viewModel.addBookToMyLibrary(bookToAdd)
                                    Toast.makeText(context, "Added to My Library!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error: Book ID not found", Toast.LENGTH_SHORT).show()
                                }
                            },
                            containerColor = Color(0xFFD98E73), // Terracotta color
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add to Library")
                        }
                    },
                    // ------------------------------------------
                    bottomBar = {
                        Button(
                            onClick = {
                                Toast.makeText(context, "Preview feature coming soon!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD98E73)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .height(56.dp)
                        ) {
                            Text(
                                "Preview Book",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // --- Shelf Display ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .background(Color(0xFFC48E68))
                            )

                            Card(
                                elevation = CardDefaults.cardElevation(12.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(240.dp)
                                    .padding(bottom = 0.dp)
                            ) {
                                if (img.isNotEmpty()) {
                                    AsyncImage(
                                        model = img,
                                        contentDescription = title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- Title & Author ---
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 28.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3B32)
                            )
                            Text(
                                text = "by $author",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // --- Rating Row ---
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "4.8",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3B32)
                                )
                                Text(
                                    " (120 Reviews)",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // --- Description ---
                            Text(
                                text = "Description",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3B32)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = description,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFF5D4037)
                            )

                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}