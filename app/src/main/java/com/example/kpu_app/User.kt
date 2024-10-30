package com.example.kpu_app

data class User(
    var id:String ="",
    var username:String="",
    var email:String = "",
    var password:String = "",
    var favoriteMovies: List<String> = mutableListOf(),
)
