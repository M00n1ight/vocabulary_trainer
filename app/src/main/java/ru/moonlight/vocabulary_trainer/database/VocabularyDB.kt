package ru.moonlight.vocabulary_trainer.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(WordData::class), version = 1)
abstract class VocabularyDB: RoomDatabase() {

    abstract fun WordDataDao(): WordDataDao

    companion object {
        private var INSTANCE: VocabularyDB? = null

        fun getInstance(context: Context): VocabularyDB? {
            if (INSTANCE == null) {
                synchronized(VocabularyDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, VocabularyDB::class.java, "vt.0.1")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }

}