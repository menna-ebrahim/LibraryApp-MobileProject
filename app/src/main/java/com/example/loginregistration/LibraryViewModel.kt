package com.example.loginregistration.Database.Enities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.Category

import com.example.loginregistration.Database.Enities.MyLibraryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.annotation.OptIn
import com.example.loginregistration.Database.BookDatabase


// ===== ViewModel =====
// ViewModel: The link between the data base and the
class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = BookDatabase.getInstance(application)
    private val daoCategory = db.CategoryDao()
    private val daobook = db.BookDao()
    private val daolibrary = db.MyLibraryDao()

    //===============Book(Admin Side)=============
    val allBooks: Flow<List<Book>> = daobook.getAllBooks()
    fun addBook(book: Book) {
        viewModelScope.launch { daobook.insertBook(book) }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch { daobook.deleteBook(book) }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch { daobook.updateBook(book) }
    }

    //===============category(Admin Side)=============
    val allCategories: Flow<List<Category>> = daoCategory.getAllCategories()
    fun addCategory(name: String, img: String) {
        viewModelScope.launch { daoCategory.insertCategory(Category(name = name, img = img)) }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { daoCategory.deleteCategory(category) }
    }

    // ========MyLibrary============
    private val currentUserId = MutableStateFlow("test_user_1")

    @OptIn(ExperimentalCoroutinesApi::class)
    val myLibraryBooks: Flow<List<Book>> = currentUserId.flatMapLatest { uid ->
        daolibrary.getMyLibraryBooks(uid)
    }

    fun updateUserId(newUid: String) {
        currentUserId.value = newUid
    }

    fun addBookToMyLibrary(book: Book) {
        viewModelScope.launch {
            daolibrary.addBookToLibrary(
                MyLibraryEntity(firebaseUid = currentUserId.value, bookId = book.id)
            )
        }
    }

    fun removeBookFromMyLibrary(bookId: Int) {
        viewModelScope.launch {
            daolibrary.removeBookFromLibrary(bookId, currentUserId.value)
        }
    }

}
