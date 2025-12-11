package com.example.loginregistration.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.loginregistration.Database.Enities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM Categories")
     fun getAllCategories(): Flow<List<Category>>


    @Delete
    suspend fun deleteCategory(category: Category)
}