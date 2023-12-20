package com.example.room1.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//berbentuk struktural database
@Entity(tableName = "movie_table")
data class MovieR (
    @PrimaryKey
    @NonNull
    var id:String ="",
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "poster")
    var poster:String = "",
    @ColumnInfo(name = "rating")
    var rating:Float = 0.0F,
    @ColumnInfo(name = "duration")
    var duration:Int = 0,
    @ColumnInfo(name = "director")
    var director:String = "",
    @ColumnInfo(name = "writer")
    var writer:String = "",
    @ColumnInfo(name = "star")
    var star:String = "",
    @ColumnInfo(name = "isFavorite")
    var isFavorite:Boolean = false
)