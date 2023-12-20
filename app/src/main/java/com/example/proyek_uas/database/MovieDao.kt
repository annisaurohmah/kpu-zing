package com.example.room1.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movie: MovieR)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: List<MovieR>)

    @Update
    fun update(movie: MovieR)

    @Delete
    fun delete(mo: MovieR)

    @get:Query("SELECT * from movie_table ORDER BY id ASC")
    val allMovies: LiveData<List<MovieR>>

    @Query("SELECT * FROM movie_table WHERE id = :movieId")
    fun getMovieById(movieId: String): MovieR?

    @Query("DELETE FROM movie_table")
    suspend fun deleteAllFilm()
}

