package com.example.room1.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

//berbentuk struktural database
@Entity(tableName = "pemilih_table")
data class Pemilih(
    @PrimaryKey
    @NonNull
    var nik: String = "",

    @ColumnInfo(name = "fullname")
    val fullname: String = "",

    @ColumnInfo(name = "nohp")
    val nohp: String = "",

    @ColumnInfo(name = "gender")
    var gender: String = "",

    @ColumnInfo(name = "registerdate")
    var registerdate: String = "",  // Jadikan nullable dengan `?`

    @ColumnInfo(name = "location")
    var location: String = "",

    @ColumnInfo(name = "picture")
    var picture: String = ""
)
