package com.example.proyek_uas


data class Movie (
    var id:String ="",
    var poster:String = "",
    var title:String = "",
    var rating:Float = 0.0F,
    var duration:Int = 0,
    var description: String = "",
    var director:String = "",
    var writer:String = "",
    var star:String = "",
    var isFavorite:Boolean = false
)