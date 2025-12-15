package com.example.loginregistration.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState // Import this
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.loginregistration.Database.BookDatabase
import com.example.loginregistration.Database.Enities.LibraryViewModel // Best practice: Use ViewModel

class CategoryBooksActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val catName = intent.getStringExtra("CAT_NAME") ?: "Books"
        val catId = intent.getIntExtra("CAT_ID", -1)


        val db = BookDatabase.getInstance(applicationContext)
        val bookDao = db.BookDao()

        setContent {

            val books by bookDao.getBooksByCategory(catId).collectAsState(initial = emptyList())

            MaterialTheme {
                Scaffold(
                    containerColor = Color(0xFFF9F5F0),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    catName,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3B32)
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF4A3B32))
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF9F5F0))
                        )
                    }
                ) { padding ->
                    if (books.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                            Text("No books in this category yet.", color = Color.Gray)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(padding)
                        ) {
                            items(books) { book ->
                                BookItem(
                                    book = book,
                                    onClick = {
                                        val intent = Intent(this@CategoryBooksActivity, BookDetailsActivity::class.java)
                                        intent.putExtra("BOOK_ID", book.id) // Pass ID for "Add to Library"
                                        intent.putExtra("TITLE", book.title)
                                        intent.putExtra("AUTHOR", book.author)
                                        intent.putExtra("DESC", book.description)
                                        intent.putExtra("IMG", book.img)
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}