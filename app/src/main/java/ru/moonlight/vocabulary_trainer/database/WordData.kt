package ru.moonlight.vocabulary_trainer.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "WordData")
data class WordData(@PrimaryKey(autoGenerate = true) var id: Int?,
                      @ColumnInfo(name = "word") var word: String,
                      @ColumnInfo(name = "translation") var translation: String
) {
    constructor(): this(null, "", "")
}
