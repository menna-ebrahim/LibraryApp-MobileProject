package com.example.loginregistration.screens

import android.os.Bundle
import androidx.activity.ComponentActivity // Import this
import androidx.activity.compose.setContent // Import this
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.LibraryViewModel
import com.example.loginregistration.R
class MyLibraryActivity : ComponentActivity() {

    val BeigeBackground = Color(0xFFF5F2EF)
    val PrimaryTerracotta = Color(0xFFC67C63)
    val DarkText = Color(0xFF4A3B32)
    val SurfaceWhite = Color(0xFFFFFFFF)
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = LibraryViewModel(application)

        setContent {
            MyLibraryScreen(
                viewModel = viewModel,
                onBackClick = { finish() }
            )
        }
    }
//compose for  myLibraryPage
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyLibraryScreen(
        viewModel: LibraryViewModel,
        onBackClick: () -> Unit
    ) {
        // Collect books from ViewModel Flow
        val books by viewModel.myLibraryBooks.collectAsState(initial = emptyList())

        Scaffold(
            containerColor = BeigeBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "My Library",
                            color = DarkText,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = DarkText
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BeigeBackground
                    )
                )
            }
        ) { paddingValues ->

            if (books.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your library is empty.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books) { book ->
                        MyLibraryBookItem(
                            book = book,
                            onRemoveClick = {
                                viewModel.removeBookFromMyLibrary(book.id)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MyLibraryBookItem(
        book: Book,
        onRemoveClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book cover image
                if (book.img != null) {
                    coil.compose.AsyncImage(
                        model = book.img,
                        contentDescription = "Book Cover",
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Book Cover",
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                // put space between items
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                TextButton(onClick = onRemoveClick) {
                    Text("REMOVE", color = Color.Red)
                }
            }
        }
    }
}