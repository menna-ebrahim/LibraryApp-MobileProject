package com.example.loginregistration.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.loginregistration.Database.Enities.MyLibraryEntity
import com.example.loginregistration.Database.Enities.Book




    @Dao
    interface MyLibraryDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun addBookToLibrary(item: MyLibraryEntity)

        @Query("""
        SELECT books.* FROM books 
        INNER JOIN my_library ON books.id = my_library.bookId 
        WHERE my_library.firebaseUid = :uid""")
        fun getMyLibraryBooks(uid: String): Flow<List<Book>>


        @Query("DELETE FROM my_library WHERE bookId = :bookId AND firebaseUid = :uid")
        suspend fun removeBookFromLibrary(bookId: Int, uid: String)
    }

