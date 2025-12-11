package com.example.loginregistration.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.loginregistration.Database.Dao.BookDao
import com.example.loginregistration.Database.Dao.CategoryDao
import com.example.loginregistration.Database.Dao.MyLibraryDao
import com.example.loginregistration.Database.Enities.Book
import com.example.loginregistration.Database.Enities.Category
import com.example.loginregistration.Database.Enities.MyLibraryEntity

@Database(entities = [Book::class, Category::class, MyLibraryEntity::class], version = 3)
abstract class BookDatabase : RoomDatabase() {

    abstract fun BookDao(): BookDao
    abstract fun CategoryDao(): CategoryDao

    abstract fun MyLibraryDao(): MyLibraryDao


    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getInstance(context: Context): BookDatabase {
            if (INSTANCE == null) {
                synchronized(lock = BookDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        klass = BookDatabase::class.java,
                        name = "movie_db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}