package com.example.room1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

//import entity yg sudah dibuat
//export schema bisa diakses dari folder atau mana
@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class MovieRoomDatabase: RoomDatabase() {
    //CRUD lewat NodeDAO
    abstract fun nodeDao(): MovieDao?

    companion object {
        @Volatile
        private var INSTANCE: MovieRoomDatabase? = null

        fun getDatabase(context: Context): MovieRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(MovieRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        MovieRoomDatabase::class.java, "note_database"
                    ).build()
                }
            }
            return INSTANCE
        }

    }
}