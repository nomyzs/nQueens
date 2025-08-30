package com.jarosz.szymon.nqueens.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameResult::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resultsDao(): ResultsDao
}
