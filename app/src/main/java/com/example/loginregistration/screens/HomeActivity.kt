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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.Category
import com.example.loginregistration.Database.Enities.LibraryViewModel
import com.example.loginregistration.screens.AdminActivity
import com.example.loginregistration.screens.BookDetailsActivity
import com.example.loginregistration.screens.CategoryBooksActivity
import com.example.loginregistration.screens.LoginActivity
import com.example.loginregistration.screens.MyLibraryActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        val viewModel = LibraryViewModel(application)

        setContent {
            val allBooks by viewModel.newestBooks.collectAsState(initial = emptyList())
            val categories by viewModel.allCategories.collectAsState(initial = emptyList())

            // --- Search State ---
            var isSearching by remember { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf("") }

            // --- Filter Logic ---
            val filteredBooks = if (searchQuery.isEmpty()) {
                allBooks
            } else {
                allBooks.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.author.contains(searchQuery, ignoreCase = true)
                }
            }

            var selectedTab by remember { mutableStateOf(0) }

            MaterialTheme {
                Scaffold(
                    containerColor = Color(0xFFF9F5F0),
                    topBar = {
                        // --- Dynamic Top Bar ---
                        if (isSearching) {
                            SearchAppBar(
                                query = searchQuery,
                                onQueryChanged = { searchQuery = it },
                                onCloseClicked = {
                                    isSearching = false
                                    searchQuery = ""
                                }
                            )
                        } else {
                            HomeTopBar(
                                onSearchClicked = { isSearching = true },
                                onLogout = {
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            // 1. Remove white background by matching app background
                            containerColor = Color(0xFFF9F5F0),
                            // 2. Remove the slight gray tint/shadow
                            tonalElevation = 0.dp
                        ) {
                            // Define clean colors for all items
                            val navColors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4A3B32), // Dark Brown when selected
                                selectedTextColor = Color(0xFF4A3B32),
                                unselectedIconColor = Color.Gray,      // Gray when not selected
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent     // REMOVES the pill background
                            )

                            // 1. HOME
                            NavigationBarItem(
                                icon = {
                                    // Fill the icon if selected, outline if not (optional polish)
                                    Icon(if(selectedTab == 0) Icons.Default.Home else Icons.Default.Home, contentDescription = "Home")
                                },
                                label = { Text("Home", fontWeight = if(selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                colors = navColors
                            )

                            // 2. MY LIBRARY
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.List, contentDescription = "My Library") },
                                label = { Text("My Library", fontWeight = if(selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                                selected = selectedTab == 1,
                                onClick = {
                                    selectedTab = 1
                                    val intent = Intent(this@HomeActivity, MyLibraryActivity::class.java)
                                    startActivity(intent)
                                },
                                colors = navColors
                            )

                            // 3. PROFILE
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile", fontWeight = if(selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                colors = navColors
                            )

                            // 4. ADMIN (Conditional)
                            if (isAdmin) {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
                                    label = { Text("Admin", fontWeight = if(selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                                    selected = selectedTab == 3,
                                    onClick = {
                                        selectedTab = 3
                                        Toast.makeText(this@HomeActivity, "Opening Admin Panel...", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@HomeActivity, AdminActivity::class.java)
                                        startActivity(intent)
                                    },
                                    colors = navColors
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // --- MAIN CONTENT ---

                        // 1. Featured Book (Hide when searching)
                        if (!isSearching && allBooks.isNotEmpty()) {
                            val featuredBook = allBooks.first()
                            Box(modifier = Modifier.clickable { openDetails(featuredBook) }) {
                                FeaturedShelf(book = featuredBook)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // 2. Books List (Shows filtered results when searching)
                        SectionHeader(title = if (isSearching && searchQuery.isNotEmpty()) "Search Results" else "Newest Books")

                        if (filteredBooks.isNotEmpty()) {
                            BooksRow(
                                books = filteredBooks,
                                onBookClick = { book -> openDetails(book) }
                            )
                        } else if (isSearching && searchQuery.isNotEmpty()) {
                            Text("No books found for \"$searchQuery\"", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                        } else if (allBooks.isEmpty()) {
                            Text("No books yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        // 3. Categories (Hide when searching)
                        if (!isSearching) {
                            Spacer(modifier = Modifier.height(24.dp))
                            SectionHeader(title = "Browse by Category")
                            if (categories.isNotEmpty()) {
                                CategoryRow(
                                    categories = categories,
                                    onCategoryClick = { category ->
                                        val intent = Intent(this@HomeActivity, CategoryBooksActivity::class.java)
                                        intent.putExtra("CAT_NAME", category.name)
                                        intent.putExtra("CAT_ID", category.id)
                                        startActivity(intent)
                                    }
                                )
                            } else {
                                Text("No categories yet.", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openDetails(book: Book) {
        val intent = Intent(this, BookDetailsActivity::class.java)
        intent.putExtra("BOOK_ID", book.id) // <--- ADD THIS LINE
        intent.putExtra("TITLE", book.title)
        intent.putExtra("AUTHOR", book.author)
        intent.putExtra("DESC", book.description)
        intent.putExtra("IMG", book.img)
        startActivity(intent)
    }
}

// ================= NEW & UPDATED UI COMPOSABLES =================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onSearchClicked: () -> Unit, onLogout: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Home",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF4A3B32)
            )
        },
        navigationIcon = {
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = Color(0xFFD98E73))
            }
        },
        actions = {
            IconButton(onClick = onSearchClicked) {
                Icon(Icons.Default.Search, "Search", tint = Color(0xFF4A3B32))
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF9F5F0))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onCloseClicked: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    TopAppBar(
        title = {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = query,
                onValueChange = onQueryChanged,
                placeholder = { Text("Search title or author...", color = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFFC67C63)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseClicked) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF4A3B32))
            }
        },
        actions = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(Icons.Default.Close, "Clear", tint = Color(0xFF4A3B32))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9F5F0))
    )
}

// --- (Keep FeaturedShelf, BookSpine, SectionHeader, BooksRow, BookItem, CategoryRow, CategoryItem as they were) ---
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
                    if (!book.img.isNullOrEmpty()) {
                        AsyncImage(model = book.img, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
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
        items(books) { book ->
            BookItem(book, onClick = { onBookClick(book) })
        }
    }
}

@Composable
fun BookItem(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(110.dp).clickable { onClick() },
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.height(160.dp).fillMaxWidth()
        ) {
            if (!book.img.isNullOrEmpty()) {
                AsyncImage(
                    model = book.img,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            book.title,
            maxLines = 1,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A3B32)
        )
        Text(
            book.author,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}


// ... inside HomeActivity.kt (at the bottom with UI Composables) ...
@Composable
fun CategoryRow(categories: List<Category>, onCategoryClick: (Category) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            CategoryItem(category, onClick = { onCategoryClick(category) })
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier.size(70.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            if (category.img.isNotEmpty()) {
                AsyncImage(
                    model = category.img,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Added .ifEmpty check so you can see if data is missing
        Text(
            text = category.name.ifEmpty { "Unnamed" },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4A3B32) // Ensuring dark text color
        )
    }
}