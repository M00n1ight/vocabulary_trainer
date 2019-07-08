package ru.moonlight.vocabulary_trainer.database

import androidx.room.*

@Dao
interface WordDataDao {
    @Query("SELECT * FROM worddata ORDER BY id DESC")
    fun getAll(): MutableList<WordData>

    @Query("DELETE FROM worddata")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: WordData)

    @Update
    fun updateWord(word: WordData)

    @Delete
    fun deleteWord(word: WordData)
}