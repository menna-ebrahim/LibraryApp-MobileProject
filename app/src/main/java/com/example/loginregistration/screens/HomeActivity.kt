package com.example.loginregistration.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
// import androidx.compose.material.icons.filled.Add // Removed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.loginregistration.Database.BookDatabase
import com.example.loginregistration.Database.Dao.BookDao
import com.example.loginregistration.Database.Dao.CategoryDao
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.Category
import com.example.loginregistration.screens.LoginActivity

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.launch // Removed
import kotlinx.coroutines.withContext

class HomeActivity : ComponentActivity() {

    private lateinit var db: BookDatabase
    private lateinit var bookDao: BookDao
    private lateinit var categoryDao: CategoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. CHECK IF USER IS ADMIN
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        db = Room.databaseBuilder(applicationContext, BookDatabase::class.java, "books_db")
            .fallbackToDestructiveMigration()
            .build()
        bookDao = db.BookDao()
        categoryDao = db.CategoryDao()

        setContent {
            var newestBooks by remember { mutableStateOf(emptyList<Book>()) }
            val categories by categoryDao.getAllCategories().collectAsState(initial = emptyList())

            var selectedTab by remember { mutableStateOf(0) }

            // Removed 'scope' since we don't need to launch insert actions anymore

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    // Only fetch the non-Flow data here
                    newestBooks = bookDao.getNewestBooks()
                    // REMOVED: categories = categoryDao.getAllCategories()
                }
            }

            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopBar(onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        })
                    },

                    // --- BOTTOM NAVIGATION BAR ---
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            contentColor = Color(0xFF4A3B32)
                        ) {
                            // 1. HOME ICON
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") },
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8D5C4))
                            )

                            // 2. CATEGORY ICON
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.List, contentDescription = "Category") },
                                label = { Text("My Library") },
                                selected = selectedTab == 1,
                                onClick = {
                                    selectedTab = 1
                                    val intent = Intent(this@HomeActivity, MyLibraryActivity::class.java)
                                    startActivity(intent)
                                },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8D5C4))
                            )

                            // 3. PROFILE ICON
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile") },
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8D5C4))
                            )

                            // 4. ADMIN PANEL ICON (Only if isAdmin == true)
                            if (isAdmin) {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
                                    label = { Text("Admin") },
                                    selected = selectedTab == 3,
                                    onClick = {
                                        selectedTab = 3
//                                        Toast.makeText(this@HomeActivity, "Opening Admin Panel...", Toast.LENGTH_SHORT).show()
                                         val intent = Intent(this@HomeActivity, AdminActivity::class.java)
                                         startActivity(intent)
                                    },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFD98E73))
                                )
                            }
                        }
                    },
                    containerColor = Color(0xFFF9F5F0)
                    // REMOVED: floatingActionButton block
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        if (newestBooks.isNotEmpty()) {
                            // Fix: Featured Book Click
                            val featuredBook = newestBooks.first()
                            Box(modifier = Modifier.clickable { openDetails(featuredBook) }) {
                                FeaturedShelf(book = featuredBook)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        SectionHeader(title = "Newest Books")
                        if(newestBooks.isNotEmpty()) {
                            // Fix: Books Row Click
                            BooksRow(
                                books = newestBooks,
                                onBookClick = { book -> openDetails(book) }
                            )
                        } else {
                            Text("No books yet.", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        SectionHeader(title = "Browse by Category")
                        if(categories.isNotEmpty()) {
                            CategoryRow(categories = categories)
                        }
                    }
                }
            }
        }
    }

    // Helper to open Book Details
    private fun openDetails(book: Book) {
        val intent = Intent(this, BookDetailsActivity::class.java)
        intent.putExtra("TITLE", book.title)
        intent.putExtra("AUTHOR", book.author)
        intent.putExtra("DESC", book.description)
        intent.putExtra("IMG", book.img)
        startActivity(intent)
    }

    // REMOVED: insertBookWithCategory function
}

// --- UI COMPONENTS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onLogout: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Home", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF4A3B32)) },
        navigationIcon = {
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = Color(0xFFD98E73))
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF9F5F0))
    )
}

@Composable
fun FeaturedShelf(book: Book) {
    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8D5C4)),
        modifier = Modifier.fillMaxWidth().height(300.dp).padding(bottom = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.Bottom) {
                    BookSpine(Color(0xFFD38B8B), 220, 28); Spacer(Modifier.width(4.dp)); BookSpine(Color.White, 200, 8)
                }
                Card(elevation = CardDefaults.cardElevation(10.dp), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(140.dp).height(210.dp)) {
                    AsyncImage(model = book.img, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    BookSpine(Color.White, 190, 8); Spacer(Modifier.width(4.dp)); BookSpine(Color(0xFFF0E5D8), 210, 24)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(Color(0xFFC48E68)))
        }
    }
}

@Composable
fun BookSpine(color: Color, height: Int, width: Int) {
    Box(modifier = Modifier.height(height.dp).width(width.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(color))
}

@Composable
fun SectionHeader(title: String) {
    Text(title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4A3B32), modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
fun BooksRow(books: List<Book>, onBookClick: (Book) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
        items(books) { book -> BookItem(book, onClick = { onBookClick(book) }) }
    }
}

@Composable
fun BookItem(book: Book, onClick: () -> Unit) {
    Column(modifier = Modifier.width(110.dp).clickable { onClick() }, horizontalAlignment = Alignment.Start) {
        Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.height(160.dp).fillMaxWidth()) {
            AsyncImage(model = book.img, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(book.title, maxLines = 1, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4A3B32))
        Text(book.author, maxLines = 1, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun CategoryRow(categories: List<Category>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
        items(categories) { category -> CategoryItem(category) }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = CircleShape, modifier = Modifier.size(70.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            AsyncImage(model = category.img, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}