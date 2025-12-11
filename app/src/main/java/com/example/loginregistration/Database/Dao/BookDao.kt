package com.example.loginregistration.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.loginregistration.Database.Enities.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    suspend fun insertBook(book: Book)

    @Query("SELECT * FROM Books")
    fun getAllBooks(): Flow<List<Book>>


    @Query("SELECT * FROM books ORDER BY createdAt DESC LIMIT 3")
    suspend fun getNewestBooks(): List<Book>


    @Query("SELECT * FROM Books WHERE Cat_Id = :catId")
    suspend fun getBooksByCategory(catId: Int): List<Book>


    @Query("SELECT * FROM Books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    suspend fun searchBooks(query: String): List<Book>


    @Delete
    suspend fun deleteBook(book: Book)



    @Update
    suspend fun updateBook(book: Book)


//    @Query("""
//    SELECT Books.*, Categories.name AS categoryName
//    FROM Books
//    INNER JOIN Categories ON Books.Cat_Id = Categories.id
//""")
//    fun getAllBooksWithCategoryName(): kotlinx.coroutines.flow.Flow<List<BookWithCategory>>

}