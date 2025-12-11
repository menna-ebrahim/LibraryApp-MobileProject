package com.example.loginregistration.Database.Enities
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "Books",
        foreignKeys = [ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["Cat_Id"],
            onDelete = ForeignKey.CASCADE
        )]
    )


    data class Book(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val Cat_Id: Int,
        val title: String,
        val description:String,
        val img: String,
        val author: String,
        val categoryName: String,
        val createdAt: Long = System.currentTimeMillis()
    )



