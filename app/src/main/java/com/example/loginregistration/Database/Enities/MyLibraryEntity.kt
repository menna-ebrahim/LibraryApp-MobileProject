package com.example.loginregistration.Database.Enities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_library")
data class MyLibraryEntity(
    @PrimaryKey(autoGenerate = true)
    val userBookId: Int = 0,
    val firebaseUid: String,
    val bookId: Int,

    )