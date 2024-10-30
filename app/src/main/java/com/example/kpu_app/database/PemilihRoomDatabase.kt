package com.example.room1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

//import entity yg sudah dibuat
//export schema bisa diakses dari folder atau mana
@Database(entities = [Pemilih::class], version = 1, exportSchema = false)
abstract class PemilihRoomDatabase: RoomDatabase() {
    //CRUD lewat NodeDAO
    abstract fun nodeDao(): PemilihDao?

    companion object {
        @Volatile
        private var INSTANCE: PemilihRoomDatabase? = null

        fun getDatabase(context: Context): PemilihRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(PemilihRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        PemilihRoomDatabase::class.java, "pemilih_database"
                    ).build()
                }
            }
            return INSTANCE
        }

    }
}