package com.example.loginregistration
import org.junit.Assert.*

import android.app.Application
import com.example.loginregistration.Database.Dao.BookDao
import com.example.loginregistration.Database.Dao.CategoryDao
import com.example.loginregistration.Database.Dao.MyLibraryDao
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.LibaryViewModelForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    @Mock
    lateinit var mockBookDao: BookDao

    @Mock
    lateinit var mockCategoryDao: CategoryDao

    @Mock
    lateinit var mockMyLibraryDao: MyLibraryDao

    @Mock
    lateinit var mockApplication: Application

    private lateinit var viewModel: LibaryViewModelForTesting

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {

        MockitoAnnotations.openMocks(this)

        Dispatchers.setMain(testDispatcher)

        viewModel = LibaryViewModelForTesting(
            application = mockApplication,
            daobook = mockBookDao,
            daoCategory = mockCategoryDao,
            daolibrary = mockMyLibraryDao
        )



    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // === Test Case 1: Admin Adds a Book ===
    @Test
    fun admin_addsBook_verifiesDaoInsertCalled() = runTest {
        // 1. Arrange
        val newBook = Book(
            id = 1,
            Cat_Id = 10,
            title = "Testing Logic",
            description = "Test Description",
            img = "image/path",
            author = "Admin",
            categoryName = "CS",
            createdAt = System.currentTimeMillis()
        )

        viewModel.addBook(newBook)

        advanceUntilIdle()

        // 3. Assert
        verify(mockBookDao).insertBook(newBook)
    }

    // === Test Case 2: Admin Deletes a Book ===
    @Test
    fun admin_deletesBook_verifiesDaoDeleteCalled() = runTest {
        // 1. Arrange
        val bookToDelete = Book(
            id = 5,
            Cat_Id = 20,
            title = "Old Book",
            description = "To be deleted",
            img = "",
            author = "Unknown",
            categoryName = "General",
            createdAt = 12345L
        )

        // 2. Act
        viewModel.deleteBook(bookToDelete)

        advanceUntilIdle()

        verify(mockBookDao).deleteBook(bookToDelete)
    }



    // === Test Case 3: Search a Book ===

    @Test
    fun searchBooksReturnsCorrectResult() = runTest {
        // 1. Arrange
        val book1 = Book(Cat_Id=1, title="Harry Potter", author="Rowling", description="", img="", categoryName="new")

        // Create the list you expect the DAO to return
        val expectedList = listOf(book1)

        // STUBBING: explicitely tell the mock what to return
        // Note: In Kotlin 'when' is a keyword, so we use backticks `when`
        Mockito.`when`(mockBookDao.searchBooks("Harry")).thenReturn(expectedList)

        // 2. Act
        // We don't need to call addBook() because the Mock doesn't store it anyway.
        // We just call search, and the Mock returns the list we "stubbed" above.
        val searchResult = viewModel.searchBooks("Harry")

        // 3. Assert
        assertNotNull(searchResult) // Ensure it's not null
        assertEquals(1, searchResult.size)
        assertEquals("Harry Potter", searchResult[0].title)
    }

    @Test
    fun searchBooksReturnsEmptyIfNotFound() = runTest {
        // 1. Arrange
        // Tell the mock to return an empty list for "Banana"
        Mockito.`when`(mockBookDao.searchBooks("Banana")).thenReturn(emptyList())

        // 2. Act
        val searchResult = viewModel.searchBooks("Banana")

        // 3. Assert
        assertNotNull(searchResult)
        assertTrue(searchResult.isEmpty())
    }

}