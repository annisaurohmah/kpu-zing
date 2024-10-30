package com.example.room1.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PemilihDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(pemilih: Pemilih)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPemilih(pemilih: List<Pemilih>)

    @Update
    fun update(pemilih: Pemilih)

    @Delete
    fun delete(mo: Pemilih)

    @get:Query("SELECT * from pemilih_table")
    val allPemilih: LiveData<List<Pemilih>>

    @Query("SELECT * FROM pemilih_table WHERE nik = :nikPemilih")
    fun getPemilihById(nikPemilih: String): Pemilih?

    @Query("DELETE FROM pemilih_table")
    suspend fun deleteAllPemilih()

}

