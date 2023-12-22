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
    fun insert(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: List<Movie>)

    @Update
    fun update(movie: Movie)

    @Delete
    fun delete(mo: Movie)

    @get:Query("SELECT * from movie_table")
    val allMovies: LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table WHERE id = :movieId")
    fun getMovieById(movieId: String): Movie?

    @Query("DELETE FROM movie_table")
    suspend fun deleteAllFilm()
}

