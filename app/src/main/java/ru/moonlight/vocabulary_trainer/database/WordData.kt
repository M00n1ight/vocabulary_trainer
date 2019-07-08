package ru.moonlight.vocabulary_trainer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WordData")
data class WordData(@PrimaryKey(autoGenerate = true) var id: Int?,
                      @ColumnInfo(name = "word") var word: String,
                      @ColumnInfo(name = "translation") var translation: String
) {
    constructor(): this(null, "", "")
}
