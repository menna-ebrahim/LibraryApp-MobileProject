package com.example.loginregistration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.loginregistration.Database.BookDatabase
import com.example.loginregistration.Database.Dao.BookDao
import com.example.loginregistration.Database.Dao.CategoryDao
import com.example.loginregistration.Database.Enities.Book // Verify your imports
import com.example.loginregistration.Database.Enities.Category
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookDaoTest {

    private lateinit var db: BookDatabase
    private lateinit var bookDao: BookDao
    private lateinit var categoryDao: CategoryDao // 1. Add CategoryDao

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BookDatabase::class.java).build()
        bookDao = db.BookDao()
        categoryDao = db.CategoryDao() // 2. Initialize it

        // 3. CRITICAL FIX: Insert a Category first!
        // We give it ID = 1 so the books can link to it.
        val testCategory = Category(name = "Test Category", img = "")
        // Note: Make sure your Category class allows you to set ID or it auto-generates to 1.
        // If it auto-generates, you might need to insert it and rely on the auto-ID.
        // Assuming you can insert:
        categoryDao.insertCategory(testCategory)
    }

    @After
    fun closeDb() {
        db.close()
    }

}