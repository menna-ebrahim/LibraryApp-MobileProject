package com.example.loginregistration.screens

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.LibraryViewModel
import com.example.loginregistration.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import android.app.Activity

class AdminActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = LibraryViewModel(application)

        setContent {

            AdminMain(viewModel)
        }
    }

    // --- Helper Color Variables ---
    val BeigeBackground = Color(0xFFF5F2EF)
    val PrimaryTerracotta = Color(0xFFC67C63)
    val DarkText = Color(0xFF4A3B32)
    val SurfaceWhite = Color(0xFFFFFFFF)

    // --- Helper Functions ---
    fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }

    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "book_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Composable
    fun AdminMain(viewModel: LibraryViewModel) {
        var currentScreen by remember { mutableStateOf("Dashboard") }
        var bookToEdit by remember { mutableStateOf<Book?>(null) }

        val context = LocalContext.current

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BeigeBackground)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ---  Back Icon---
                    IconButton(
                        onClick = {

                            (context as? Activity)?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkText
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { currentScreen = "Dashboard" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentScreen == "Dashboard") PrimaryTerracotta else Color.Transparent,
                                contentColor = if (currentScreen == "Dashboard") Color.White else DarkText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Dashboard", fontWeight = FontWeight.Bold) }

                        Button(
                            onClick = { currentScreen = "Categories" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentScreen == "Categories") PrimaryTerracotta else Color.Transparent,
                                contentColor = if (currentScreen == "Categories") Color.White else DarkText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Categories", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).background(BeigeBackground)) {
                when (currentScreen) {
                    "Dashboard" -> DashboardScreen(
                        viewModel = viewModel,
                        onAddBookClick = { bookToEdit = null; currentScreen = "AddBook" },
                        onEditBookClick = { book -> bookToEdit = book; currentScreen = "AddBook" }
                    )
                    "AddBook" -> AddEditBookScreen(
                        viewModel = viewModel,
                        bookToEdit = bookToEdit,
                        onSave = { currentScreen = "Dashboard" },
                        onCancel = { currentScreen = "Dashboard" }
                    )
                    "Categories" -> CategoriesScreen(viewModel)
                }
            }
        }
    }

    // --- DASHBOARD SCREEN ---
    @Composable
    fun DashboardScreen(
        viewModel: LibraryViewModel,
        onAddBookClick: () -> Unit,
        onEditBookClick: (Book) -> Unit
    ) {
        val books by viewModel.allBooks.collectAsState(initial = emptyList())

        Column(modifier = Modifier.fillMaxSize().background(BeigeBackground).padding(16.dp)) {
            Button(
                onClick = onAddBookClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTerracotta),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Add New Book", fontSize = 18.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(books) { book ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!book.img.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = book.img,
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp).padding(end = 12.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_launcher_background),
                                        contentDescription = "Placeholder",
                                        modifier = Modifier.size(60.dp).padding(end = 12.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(book.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText)
                                    Text(book.author, color = Color.Gray, fontSize = 14.sp)
                                    Text(book.categoryName, color = PrimaryTerracotta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Text("Added: ${convertLongToDate(book.createdAt)}", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { onEditBookClick(book) }) { Text("Edit", color = DarkText) }
                                TextButton(onClick = { viewModel.deleteBook(book) }) { Text("Delete", color = Color.Red) }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- ADD/EDIT BOOK SCREEN ---
    @Composable
    fun AddEditBookScreen(
        viewModel: LibraryViewModel,
        bookToEdit: Book?,
        onSave: () -> Unit,
        onCancel: () -> Unit
    ) {
        var title by remember { mutableStateOf(bookToEdit?.title ?: "") }
        var author by remember { mutableStateOf(bookToEdit?.author ?: "") }
        var description by remember { mutableStateOf(bookToEdit?.description ?: "") }
        var selectedCategoryName by remember { mutableStateOf(bookToEdit?.categoryName ?: "") }
        var selectedCategoryId by remember { mutableStateOf(bookToEdit?.Cat_Id ?: -1) }
        var createdAtDate by remember { mutableStateOf(bookToEdit?.createdAt ?: System.currentTimeMillis()) }
        var imageUri by remember { mutableStateOf(bookToEdit?.img) }

        val categories by viewModel.allCategories.collectAsState(initial = emptyList())
        val context = LocalContext.current

        val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri = saveImageToInternalStorage(context, it) }
        }

        Column(modifier = Modifier.fillMaxSize().background(BeigeBackground).padding(16.dp)) {
            Text(if (bookToEdit == null) "Add New Book" else "Edit Book", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(modifier = Modifier.height(16.dp))

            val colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = PrimaryTerracotta, unfocusedBorderColor = Color.LightGray
            )

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), colors = colors)
            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author") }, modifier = Modifier.fillMaxWidth(), colors = colors)
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), colors = colors)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Select Category:", color = DarkText, fontWeight = FontWeight.Bold)
            LazyRow {
                items(categories) { cat ->
                    Button(
                        onClick = { selectedCategoryName = cat.name; selectedCategoryId = cat.id },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategoryId == cat.id) PrimaryTerracotta else Color.LightGray
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) { Text(cat.name) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp).clickable { imagePickerLauncher.launch("image/*") }.background(SurfaceWhite),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Text("Tap to select image", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        if (title.isNotEmpty() && author.isNotEmpty() && selectedCategoryId != -1) {
                            val newBook = Book(
                                id = bookToEdit?.id ?: 0,
                                Cat_Id = selectedCategoryId,
                                categoryName = selectedCategoryName,
                                title = title, author = author, description = description,
                                createdAt = createdAtDate, img = imageUri ?: ""
                            )
                            if (bookToEdit == null) viewModel.addBook(newBook) else viewModel.updateBook(newBook)
                            onSave()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTerracotta)
                ) { Text("Save") }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
            }
        }
    }

    // --- CATEGORIES SCREEN ---
    @Composable
    fun CategoriesScreen(viewModel: LibraryViewModel) {
        val categories by viewModel.allCategories.collectAsState(initial = emptyList())
        var newCategoryName by remember { mutableStateOf("") }
        var newCategoryImageUri by remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current

        val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { newCategoryImageUri = saveImageToInternalStorage(context, it) }
        }

        Column(modifier = Modifier.fillMaxSize().background(BeigeBackground).padding(16.dp)) {
            Text("Manage Categories", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceWhite)
                        .clickable { imagePickerLauncher.launch("image/*") }
                        .border(1.dp, PrimaryTerracotta, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (newCategoryImageUri != null) AsyncImage(model = newCategoryImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Icon(painter = painterResource(android.R.drawable.ic_menu_camera), contentDescription = null, tint = Color.Gray)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("New Category") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTerracotta)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newCategoryName.isNotEmpty()) {
                            viewModel.addCategory(newCategoryName, newCategoryImageUri ?: "")
                            newCategoryName = ""; newCategoryImageUri = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTerracotta),
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Add") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(categories) { category ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (category.img.isNotEmpty()) {
                                    AsyncImage(model = category.img, contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                Text(category.name, fontSize = 18.sp, color = DarkText)
                            }
                            IconButton(onClick = { viewModel.deleteCategory(category) }) { Text("✕", color = Color.Red, fontSize = 20.sp) }
                        }
                    }
                }
            }
        }
    }

}