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
import com.example.loginregistration.Database.Dao.BookDao
import com.example.loginregistration.Database.Dao.CategoryDao
import com.example.loginregistration.Database.Dao.MyLibraryDao


// ===== ViewModel =====
// ViewModel: The link between the data base and the
class LibaryViewModelForTesting(application: Application, private val daobook: BookDao = BookDatabase.getInstance(application).BookDao(),
                       private val daoCategory: CategoryDao = BookDatabase.getInstance(application).CategoryDao(),
                       private val daolibrary: MyLibraryDao = BookDatabase.getInstance(application).MyLibraryDao()) : AndroidViewModel(application) {


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

    suspend fun searchBooks(bookname: String): List<Book> {

        return daobook.searchBooks(bookname)
    }
}






